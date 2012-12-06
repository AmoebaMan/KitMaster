package com.amoebaman.kitmaster;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.amoebaman.kitmaster.enums.Attribute;
import com.amoebaman.kitmaster.enums.GiveKitContext;
import com.amoebaman.kitmaster.enums.GiveKitResult;
import com.amoebaman.kitmaster.enums.PermsResult;
import com.amoebaman.kitmaster.handlers.HistoryHandler;
import com.amoebaman.kitmaster.handlers.InventoryUtil;
import com.amoebaman.kitmaster.handlers.KitHandler;
import com.amoebaman.kitmaster.handlers.SignHandler;
import com.amoebaman.kitmaster.handlers.TimeStampHandler;
import com.amoebaman.kitmaster.objects.Book;
import com.amoebaman.kitmaster.objects.GiveKitEvent;
import com.amoebaman.kitmaster.objects.Kit;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

//TODO Javadoc for EVERYTHING
//TODO Implement functionality for Attribute.UPGRADE_PARENT

/**
 * 
 * The main class for KitMaster
 * 
 * @author Dennison
 *
 */
public class KitMaster extends JavaPlugin implements Listener{

	private static PluginLogger log;
	private static FileConfiguration config;

	protected static String mainDirectory, bookDirectory, kitsDirectory, dataDirectory;
	protected static File configFile, signsFile, timestampsFile, historyFile;

	protected static boolean vaultEnabled;
	protected static Permission perms;
	protected static Chat chat;
	protected static Economy economy;
	
	public final static boolean DEBUG_PERMS = false;
	public final static boolean DEBUG_KITS = false;

	@Override
	public void onEnable(){
		log = new PluginLogger(this);
		config = getConfig();

		mainDirectory = getDataFolder().getPath();
		bookDirectory = mainDirectory + "/books";
		kitsDirectory = mainDirectory + "/kits";
		dataDirectory = mainDirectory + "/data";
		
		new File(mainDirectory).mkdir();
		new File(bookDirectory).mkdir();
		new File(kitsDirectory).mkdir();
		new File(dataDirectory).mkdir();
		
		configFile = new File(mainDirectory + "/config.yml");
		signsFile = new File(dataDirectory + "/signs.yml");
		timestampsFile = new File(dataDirectory + "/timestamps.yml");
		historyFile = new File(dataDirectory + "/history.yml");

		try{
			if(!configFile.exists())
				configFile.createNewFile();
			getConfig().load(configFile);
			getConfig().options().copyDefaults(true);
			getConfig().save(configFile);
			log.info("Loaded configuration from " + configFile.getPath());
			config = getConfig();
			Book.setLineBreakSequence(config.getString("books.lineBreakChar", "|n|"));

			KitHandler.loadKits(new File(kitsDirectory));
			log.info("Loaded all kit files from " + kitsDirectory);
			
			if(!signsFile.exists())
				signsFile.createNewFile();
			SignHandler.load(signsFile);
			log.info("Loaded kit selection sign locations from " + signsFile.getPath());
			log.info("Purged " + SignHandler.purgeAbsentees() + " absent kit signs");

			if(!timestampsFile.exists())
				timestampsFile.createNewFile();
			TimeStampHandler.load(timestampsFile);
			log.info("Loaded player kit timestamps from " + timestampsFile.getPath());
			
			if(!historyFile.exists())
				historyFile.createNewFile();
			HistoryHandler.load(historyFile);
			log.info("Loaded player kit history from " + historyFile.getPath());
		}
		catch(Exception e){e.printStackTrace();}
		
		setupVaultProviders();	
		KitMasterEventHandler.registerEvents(this);
		KitMasterCommandHandler.registerCommands(this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new InfiniteEffects(), 15, 15);
	}

	@Override
	public void onDisable() {
		if(getConfig().getBoolean("clearKits.onDisable", true))
			for(OfflinePlayer player : HistoryHandler.getPlayers())
				if(player instanceof Player)
					clearAll((Player) player);
		try{
			SignHandler.save(signsFile);
			TimeStampHandler.save(timestampsFile);
			HistoryHandler.save(historyFile);
		}
		catch(Exception e){ e.printStackTrace(); }
	}

	public void reloadConfig(){
		super.reloadConfig();
		config = getConfig();
	}
	
	public static PluginLogger logger(){ return log; }
	
	public static FileConfiguration config(){ return config; }
	
	public static Plugin plugin(){ return Bukkit.getPluginManager().getPlugin("KitMaster"); }
	
	public static boolean isVaultEnabled(){ return vaultEnabled; }
	
	/**
	 * Gives a player a kit.  This method will consider and apply all attributes of the given kit, including timeouts, permissions, and inheritance.  If the kit has a parent kit, a recursive call will be made to this method <i>prior</i> to the application of <code>kit</code>, with <code>GiveKitContext.PARENT_GIVEN</code>..
	 * @param player The player to give the kit to.
	 * @param kit The kit that will be given.
	 * @param context The context or reason for giving this kit.
	 * @return a GiveKitResult signifying the success or reason for failure of giving the kit.
	 */
	public static GiveKitResult giveKit(Player player, Kit kit, GiveKitContext context){
		/*
		 * We can't give a player a null kit
		 * Return a result that reflects this
		 */
		if(kit == null)
			return GiveKitResult.FAIL_NULL_KIT;
		if(DEBUG_KITS)
			log.info("Attempting to give " + player.getName() + " the " + kit.name + " kit");
		/*
		 * Clone the kit to prevent accidental damage to the base kit
		 */
		kit = kit.clone();
		/*
		 * Check if the player has permission to take this kit in the given manner
		 * Ignore these checks if the context overrides them
		 */
		if(!context.overrides){
			PermsResult getKitPerms = KitHandler.getKitPerms(player, kit);
			switch(getKitPerms){
			case COMMAND_ONLY:
				if(context == GiveKitContext.SIGN_TAKEN){
					player.sendMessage(ChatColor.ITALIC + "You can't take the " + kit.name + " kit from signs");
					return GiveKitResult.FAIL_NO_PERMS;
				}
				break;
			case SIGN_ONLY:
				if(context == GiveKitContext.COMMAND_TAKEN){
					player.sendMessage(ChatColor.ITALIC + "You can't take the " + kit.name + " kit by command");
					return GiveKitResult.FAIL_NO_PERMS;
				}
				break;
			case INHERIT_COMMAND_ONLY:
				if(context == GiveKitContext.SIGN_TAKEN){
					player.sendMessage(ChatColor.ITALIC + "You can't take " + kit.getParent().name + " kits from signs");
					return GiveKitResult.FAIL_NO_PERMS;
				}
			case INHERIT_SIGN_ONLY:
				if(context == GiveKitContext.COMMAND_TAKEN){
					player.sendMessage(ChatColor.ITALIC + "You can't take " + kit.getParent().name + " kits by command");
					return GiveKitResult.FAIL_NO_PERMS;
				}
				break;
			case NONE:
				player.sendMessage(ChatColor.ITALIC + "You can't take the " + kit.name + " kit");
				return GiveKitResult.FAIL_NO_PERMS;
			case INHERIT_NONE:
				player.sendMessage(ChatColor.ITALIC + "You can't take " + kit.getParent().name + " kits");
				return GiveKitResult.FAIL_NO_PERMS;
			default:
			}
		}
		/*
		 * Perform operations for the parent kit
		 * Obviously these don't need to happen if there is no parent kit
		 */
		Kit parentKit = kit.getParent();
		if(parentKit != null)
			/*
			 * Check timeouts for the parent kit
			 * Don't perform these checks if the context overrides them or the player has an override permission
			 */
			if(!context.overrides && !player.hasPermission("kitmaster.notimeout")){
				switch(TimeStampHandler.timeoutCheck(player, parentKit)){
				case FAIL_TIMEOUT:
					player.sendMessage(ChatColor.ITALIC + "You need to wait " + TimeStampHandler.timeoutLeft(player, parentKit) + " more seconds before using a " + parentKit.name + " kit");
					return GiveKitResult.FAIL_TIMEOUT;
				case FAIL_SINGLE_USE:
					player.sendMessage(ChatColor.ITALIC + "You can only use a " + parentKit.name + " kit once");
					return GiveKitResult.FAIL_SINGLE_USE;
				default: }
			}
		/*
		 * Check timeouts for the current kit
		 * Don't perform these checks if the context overrides them or the player has an override permission
		 */
		if(!context.overrides && !player.hasPermission("kitmaster.notimeout")){
			switch(TimeStampHandler.timeoutCheck(player, kit)){
			case FAIL_TIMEOUT:
				player.sendMessage(ChatColor.ITALIC + "You need to wait " + TimeStampHandler.timeoutLeft(player, kit) + " more seconds before using the " + kit.name + " kit");
				return GiveKitResult.FAIL_TIMEOUT;
			case FAIL_SINGLE_USE:
				player.sendMessage(ChatColor.ITALIC + "You can only use the " + kit.name + " kit once");
				return GiveKitResult.FAIL_SINGLE_USE;
			default: }
		}
		/*
		 * Check if the player can afford the kit
		 * Don't perform these checks if the economy is not enabled, or if the contexts overrides them or the player has an override permission
		 */
		if(economy != null)
			if(economy.getBalance(player.getName()) < kit.doubleAttribute(Attribute.COST) && !player.hasPermission("kitmaster.nocharge")){
				player.sendMessage(ChatColor.ITALIC + "You need " + kit.doubleAttribute(Attribute.COST) + " " + economy.currencyNameSingular() + " to take the " + kit.name + " kit");
				return GiveKitResult.FAIL_COST;
			}
		/*
		 * Create and call a GiveKitEvent so that other plugins can modify or attempt to cancel the kit
		 * If the event comes back cancelled and the context doesn't override it, end here
		 */
		GiveKitEvent kitEvent = new GiveKitEvent(player, kit, context);
		kitEvent.callEvent();
		if (kitEvent.isCancelled() && !context.overrides)
			return GiveKitResult.FAIL_CANCELLED;
		/*
		 * Apply the kit's clearing properties
		 * Don't perform this operation if the kit is a parent
		 */
		if(context != GiveKitContext.PARENT_GIVEN)
			applyKitClears(player, kit);
		/*
		 * Apply the parent kit
		 */
		if(parentKit != null)
			giveKit(player, parentKit, GiveKitContext.PARENT_GIVEN);
		/*
		 * Add the kit's items to the player's inventory
		 */
		InventoryUtil.addItemsToInventory(player, kitEvent.getKit().items, parentKit != null && kit.booleanAttribute(Attribute.UPGRADE_PARENT));
		/*
		 * Apply the kit's potion effects to the player
		 */
		player.addPotionEffects(kitEvent.getKit().effects);
		/*
		 * Grant the kit's permissions to the player
		 * Don't perform this operation if the permission handle is not enabled
		 */
		if(perms != null)
			for(String node : kit.permissions)
				perms.playerAdd(player, node);
		/*
		 * Apply the kit's economic attributes
		 * Don't perform this operation if the economy handle is not enabled, or if the player has  an override permission
		 */
		if(economy != null && !player.hasPermission("kitmaster.nocharge")){
			economy.bankWithdraw(player.getName(), kit.doubleAttribute(Attribute.COST));
			economy.bankDeposit(player.getName(), kit.doubleAttribute(Attribute.CASH));
		}
		/*
		 * Record that this kit was taken
		 * Stamp the time, and add the kit to the player's history
		 */
		TimeStampHandler.setTimeStamp(kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT) ? null : player, kit);
		HistoryHandler.addToHistory(player, kit);
		/*
		 * Notify the player of their good fortune
		 */
		if(context == GiveKitContext.COMMAND_TAKEN || context == GiveKitContext.SIGN_TAKEN)
			player.sendMessage(ChatColor.ITALIC + kit.name + " kit taken");
		if(context == GiveKitContext.COMMAND_GIVEN || context == GiveKitContext.PLUGIN_GIVEN || context == GiveKitContext.PLUGIN_GIVEN_OVERRIDE)
			player.sendMessage(ChatColor.ITALIC + "You were given the " + kit.name + " kit");
		/*
		 * Return the success of the mission
		 */
		return GiveKitResult.SUCCESS;
	}
	
	/**
	 * Applies clearing attributes of a kit to a player.
	 * @param player The target player.
	 * @param kit The kit whose clearing attributes should be considered.
	 */
	public static void applyKitClears(Player player, Kit kit){
		boolean all = kit.booleanAttribute(Attribute.CLEAR_ALL);
		
		//Clear the player's inventory
		if(kit.booleanAttribute(Attribute.CLEAR_INVENTORY) || all){
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		}
		
		//Clear the player's potion effects
		if(kit.booleanAttribute(Attribute.CLEAR_EFFECTS) || all){
			for(PotionEffect effect : player.getActivePotionEffects())
				player.removePotionEffect(effect.getType());
		}

		//Clear the player's kit-applied permissions
		if(perms != null)
			if(kit.booleanAttribute(Attribute.CLEAR_PERMISSIONS) || all){
				for(Kit last : HistoryHandler.getHistory(player))
					for(String node : last.permissions)
						perms.playerRemove(player, node);
			}
		
		if(all)
			HistoryHandler.resetHistory(player);
	}

	/**
	 * Clears all attributes for all kits in history and clears the history for a player.  This call is equivalent to calling <code>applyKitClears</code> with a kit that has the <code>CLEAR_ALL</code> attribute.
	 * @param player The target player.
	 */
	public static void clearAll(Player player){
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		for(PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		if(perms != null)
			for(Kit last : HistoryHandler.getHistory(player))
				for(String node : last.permissions)
					perms.playerRemove(player, node);
		HistoryHandler.resetHistory(player);
	}

	/**
	 * Gets the Book object saved to file with the given name.
	 * @param name The name of the Book to retrieve.
	 * @return The book that matches the given name, or a blank written book if no book matched.
	 */
	public static ItemStack getBook(String name){
		File bookFile = new File(bookDirectory + "/" + name + ".book");
		Book book = new Book(bookFile);
		return book.getItemStack();
	}

	/**
	 * Retrieves and stores service providers obtained using the <b>Vault</b> API.
	 */
	public static void setupVaultProviders(){
		vaultEnabled = Bukkit.getPluginManager().isPluginEnabled("Vault");
		if(!vaultEnabled)
			return;
		RegisteredServiceProvider<Permission> tempPerm = Bukkit.getServicesManager().getRegistration(Permission.class);
		if(tempPerm != null){
			perms = tempPerm.getProvider();
			if(perms != null)
				log.info("Hooked into Permissions manager: " + perms.getName());
		}
		RegisteredServiceProvider<Chat> tempChat = Bukkit.getServicesManager().getRegistration(Chat.class);
		if(tempChat != null){
			chat = tempChat.getProvider();
			if(chat != null)
				log.info("Hooked into Chat manager: " + chat.getName());
		}
		RegisteredServiceProvider<Economy> tempEcon = Bukkit.getServicesManager().getRegistration(Economy.class);
		if(tempEcon != null){
			economy = tempEcon.getProvider();
			if(economy != null)
				log.info("Hooked into Economy manager: " + economy.getName());
		}
	}

	/**
	 * Private <i>Runnable</i> that is used to handle infinitely renewed potion effects.
	 * @author Dennison
	 */
	private static class InfiniteEffects implements Runnable{
		@Override
		public void run() {
			for(World world : Bukkit.getWorlds())
				for(Player player : world.getPlayers())
					for(Kit kit : HistoryHandler.getHistory(player))
						if(kit != null && kit.booleanAttribute(Attribute.INFINITE_EFFECTS))
							for(PotionEffect effect : kit.effects){
								for(PotionEffect active : player.getActivePotionEffects())
									if(effect.getType().getId() == active.getType().getId() && effect.getAmplifier() >= active.getAmplifier())
										player.removePotionEffect(active.getType());
								player.addPotionEffect(effect);
							}
		}
	}

	
}
