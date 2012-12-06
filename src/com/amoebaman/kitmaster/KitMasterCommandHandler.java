package com.amoebaman.kitmaster;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.amoebaman.kitmaster.enums.Attribute;
import com.amoebaman.kitmaster.enums.GenericResult;
import com.amoebaman.kitmaster.enums.GiveKitContext;
import com.amoebaman.kitmaster.enums.GiveKitResult;
import com.amoebaman.kitmaster.enums.PermsResult;
import com.amoebaman.kitmaster.handlers.InventoryUtil;
import com.amoebaman.kitmaster.handlers.KitHandler;
import com.amoebaman.kitmaster.objects.Book;
import com.amoebaman.kitmaster.objects.Kit;
import com.amoebaman.kitmaster.utilities.CommandHandler;
import com.google.common.collect.Lists;

public class KitMasterCommandHandler implements CommandExecutor{

	public static KitMaster plugin;
	private static HashMap<Command, Method> commandMap;

	protected static void registerCommands(KitMaster instance){
		/*
		 * Initialize the fields
		 */
		plugin = instance;
		commandMap = new HashMap<Command, Method>();
		/*
		 * For every method in this class...
		 */
		for(Method method : KitMasterCommandHandler.class.getMethods()){
			/*
			 * Grab the CommandHandler annotation
			 */
			CommandHandler annotation = method.getAnnotation(CommandHandler.class);
			/*
			 * If the annotation is there and the method contains the correct configuration of parameters...
			 */
			Class<?>[] params = method.getParameterTypes();
			if(annotation != null && instance.getCommand(annotation.name()) != null
					&& CommandSender.class.isAssignableFrom(params[0])  //First parameter is a subclass of CommandSender
					&& params[1].equals(String[].class)  //Second parameter is the argument list
					){
				/*
				 * Make sure the command gets sent to this executor
				 */
				instance.getCommand(annotation.name()).setExecutor(new KitMasterCommandHandler());
				/*
				 * Adjust the command's properties according to the annotation
				 */
				if(!(annotation.aliases().equals(new String[]{""})))
					instance.getCommand(annotation.name()).setAliases(Lists.newArrayList(annotation.aliases()));
				if(!annotation.description().equals(""))
					instance.getCommand(annotation.name()).setDescription(annotation.description());
				if(!annotation.usage().equals(""))
					instance.getCommand(annotation.name()).setUsage(annotation.usage());
				if(!annotation.permission().equals(""))
					instance.getCommand(annotation.name()).setPermission(annotation.permission());
				if(!annotation.permissionMessage().equals(""))
					instance.getCommand(annotation.name()).setPermissionMessage(annotation.permissionMessage());
				/*
				 * Finalize the command's registration
				 */
				commandMap.put(instance.getCommand(annotation.name()), method);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		/*
		 * Get the method that has been mapped to this command
		 */
		Method method = commandMap.get(command);
		/*
		 * If the method is there...
		 */
		if(method != null){
			/*
			 * We can't process the command if it has specified a player sender and the sender isn't a player
			 */
			if(method.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "This command requires a player context");
				return true;
			}
			/*
			 * Invoke the method that corresponds to the command, passing it the sender and the arguments
			 */
			try{ method.invoke(this, sender, args); }
			catch(Exception e){ e.printStackTrace(); }
		}
		/*
		 * Otherwise the method hasn't been defined, so tell the player the command doesn't exist
		 */
		else
			sender.sendMessage("Unknown command. Type \"help\" for help.");
		
		return true;
	}

	@CommandHandler(name = "givekit")
	public void givekit(CommandSender sender, String[] args){
		if (args.length < 2){
			sender.sendMessage(ChatColor.ITALIC + plugin.getCommand("givekit").getUsage());
			return;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(!KitHandler.isKit(args[1])){
			sender.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		Kit kit = KitHandler.getKit(args[1]);
		GiveKitResult result = KitMaster.giveKit(target, kit, GiveKitContext.COMMAND_GIVEN);
		if(KitMaster.DEBUG_KITS)
			KitMaster.logger().info("Result: " + result.name());
		target.sendMessage(ChatColor.ITALIC + "You have been given the " + kit.name + " kit");
		sender.sendMessage(ChatColor.ITALIC + target.getName() + " has been given the " + kit.name + " kit");
		return;
	}

	@CommandHandler(name = "kit")
	public void kit(Player player, String[] args){
		if (args.length < 1)
			return;
		if(!KitHandler.isKit(args[0])){
			player.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		Kit kit = KitHandler.getKit(args[0]);
		GiveKitResult result = KitMaster.giveKit(player, kit, GiveKitContext.COMMAND_TAKEN);
		if(KitMaster.DEBUG_KITS)
			KitMaster.logger().info("Result: " + result.name());
		return;
	}

	@CommandHandler(name = "kitlist")
	public void kitlist(CommandSender sender, String[] args){
		sender.sendMessage(ChatColor.GREEN + "Available kits:");
		for(Kit kit : KitHandler.getKits()){
			PermsResult perms = KitHandler.getKitPerms(sender, kit);
			if(perms.generic.bool && kit.booleanAttribute(Attribute.SHOW_IN_LIST) && (kit.getParent() == null || kit.getParent().booleanAttribute(Attribute.SHOW_IN_LIST))){
				String message = ChatColor.ITALIC + " - " + kit.name;
				if(perms.generic == GenericResult.CONDITIONAL)
					message += " - " + ChatColor.YELLOW + ChatColor.ITALIC + perms.message;
				sender.sendMessage(message);
			}
		}
		if(sender.hasPermission("kitmaster.take"))
			sender.sendMessage(ChatColor.GREEN + "Use '/kit <kitname>' to take a kit");
		if(sender.hasPermission("kitmaster.info"))
			sender.sendMessage(ChatColor.GREEN + "Use '/kitinfo <kitname>' to see more about a kit");
	}

	@CommandHandler(name = "kitinfo")
	public void kitinfo(CommandSender sender, String[] args){
		if(args.length < 1){
			sender.sendMessage(ChatColor.ITALIC + "Specify a kit - /kitinfo <kitname>");
			return;
		}
		Kit kit = KitHandler.getKit(args[0]);
		if(KitHandler.getKitPerms(sender, kit).generic == GenericResult.NO){
			sender.sendMessage(ChatColor.ITALIC + "You don't have permission to view the " + kit.name + " kit");
			return;
		}
		if(kit == null){
			sender.sendMessage(ChatColor.ITALIC + "That kit does not exist");
			return;
		}
		sender.sendMessage(ChatColor.ITALIC + "Kit info for " + kit.name);
		sender.sendMessage(ChatColor.ITALIC + "Items:");
		for(ItemStack item : kit.items)
			sender.sendMessage(ChatColor.ITALIC + "  - " + InventoryUtil.itemToString(item));
		sender.sendMessage(ChatColor.ITALIC + "Effects:");
		for(PotionEffect effect : kit.effects)
			sender.sendMessage(ChatColor.ITALIC + "  - " + InventoryUtil.effectToString(effect));
		sender.sendMessage(ChatColor.ITALIC + "Permissions:");
		for(String perm : kit.permissions)
			sender.sendMessage(ChatColor.ITALIC + "  - " + perm);
		for(Attribute attribute : kit.attributes.keySet())
			sender.sendMessage(ChatColor.ITALIC + attribute.toString() + ": " + kit.getAttribute(attribute));
	}

	@CommandHandler(name = "reloadkits")
	public void reloadkits(CommandSender sender, String[] args){
		KitHandler.loadKits(new File(KitMaster.kitsDirectory));
		KitMaster.logger().info("Loaded all kit files from " + KitMaster.kitsDirectory);
		sender.sendMessage(ChatColor.ITALIC + "Kits have been reloaded");
	}

	@CommandHandler(name = "savebook")
	public void savebook(Player player, String[] args){
		if(player.getItemInHand().getType() != Material.WRITTEN_BOOK){
			player.sendMessage(ChatColor.ITALIC + "You need to hold a written book before using this command");
			return;
		}
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + plugin.getCommand("savebook").getUsage());
			return;
		}
		Book book = new Book(player.getItemInHand());
		File bookFile = new File(KitMaster.bookDirectory + "/" + args[0] + ".book");
		book.saveToFile(bookFile);
		player.sendMessage(ChatColor.ITALIC + "Successfully saved " + book.getTitle() + " to file");
	}

	@CommandHandler(name = "loadbook")
	public void loadbook(Player player, String[] args){
		if(args.length == 0){
			player.sendMessage(ChatColor.ITALIC + plugin.getCommand("loadbook").getUsage());
			return;
		}
		File bookFile = new File(KitMaster.bookDirectory + "/" + args[0] + ".book");
		if(!bookFile.exists()){
			player.sendMessage(ChatColor.ITALIC + "There is no saved book by that name");
			return;
		}
		Book book = new Book(bookFile);
		player.getInventory().addItem(book.getItemStack());
		player.sendMessage(ChatColor.ITALIC + "Successfully loaded " + book.getTitle() + " from file");
	}
	
}