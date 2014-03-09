package net.amoebaman.kitmaster;

import java.util.ArrayList;
import java.util.List;

import net.amoebaman.kitmaster.controllers.ItemController;
import net.amoebaman.kitmaster.enums.Attribute;
import net.amoebaman.kitmaster.enums.ClearKitsContext;
import net.amoebaman.kitmaster.enums.GiveKitContext;
import net.amoebaman.kitmaster.enums.GiveKitResult;
import net.amoebaman.kitmaster.handlers.HistoryHandler;
import net.amoebaman.kitmaster.handlers.KitHandler;
import net.amoebaman.kitmaster.handlers.MessageHandler;
import net.amoebaman.kitmaster.handlers.SignHandler;
import net.amoebaman.kitmaster.handlers.TimeStampHandler;
import net.amoebaman.kitmaster.objects.Kit;
import net.amoebaman.kitmaster.sql.SQLQueries;
import net.amoebaman.utils.S_Loc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class KitMasterEventHandler implements Listener {
	
	protected static void init(KitMaster plugin) {
		Bukkit.getPluginManager().registerEvents(new KitMasterEventHandler(), plugin);
	}
	
	@EventHandler
	public void kitSelectionFromSigns(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Kit kit = SignHandler.getKitSign(event.getClickedBlock().getLocation());
			if (kit != null) {
				GiveKitResult result = Actions.giveKit(event.getPlayer(), kit, GiveKitContext.SIGN_TAKEN);
				if (KitMaster.DEBUG_KITS)
					KitMaster.logger().info("Result: " + result.name());
			}
			else if(event.getClickedBlock().getState() instanceof Sign && KitMaster.plugin().getConfig().getBoolean("fix-signs-mode", false)){
				for(int i = 0; i < 4; i++){
					String format = ChatColor.translateAlternateColorCodes('&', KitMaster.plugin().getConfig().getString("kitSelectionSignText.line_" + (i+1), ""));
					String text = ChatColor.translateAlternateColorCodes('&', ((Sign) event.getClickedBlock().getState()).getLine(i));
					if(ChatColor.stripColor(format).equals("%kit%"))
						kit = KitHandler.getKit(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text)));
					else if(!format.equals(text))
						return;
				}
				if(kit != null){
					if(KitMaster.isSQLRunning()){
						String loc = S_Loc.stringSave(event.getClickedBlock().getLocation(), true, false);
						KitMaster.getSQL().executeCommand(SQLQueries.SET_SIGN_AT.replaceAll(SQLQueries.LOCATION_MACRO, loc).replace(SQLQueries.KIT_MACRO, kit.name));
					}
					event.getPlayer().sendMessage(MessageHandler.getPrefix() + "Repaired kit selection sign for " + kit.name + " at " + S_Loc.stringSave(event.getClickedBlock().getLocation(), true, false));
				}
			}
		}
	}
	
	@EventHandler
	public void createKitSelectionSigns(SignChangeEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (event.getLine(0).equalsIgnoreCase("kit")) {
			if (!player.hasPermission("kitmaster.createsign")) {
				block.breakNaturally();
				player.sendMessage(MessageHandler.getMessage("make_sign.fail_perms"));
				return;
			}
			if (!KitHandler.isKit(event.getLine(1))) {
				block.breakNaturally();
				player.sendMessage(MessageHandler.getMessage("make_sign.fail_badkit"));
				return;
			}
			Kit kit = KitHandler.getKit(event.getLine(1));
			SignHandler.saveKitSign(kit, event.getBlock().getLocation());
			event.setLine(0, ChatColor.translateAlternateColorCodes('&', KitMaster.config().getString("kitSelectionSignText.line_1")).replace("%kit%", kit.name));
			event.setLine(1, ChatColor.translateAlternateColorCodes('&', KitMaster.config().getString("kitSelectionSignText.line_2")).replace("%kit%", kit.name));
			event.setLine(2, ChatColor.translateAlternateColorCodes('&', KitMaster.config().getString("kitSelectionSignText.line_3")).replace("%kit%", kit.name));
			event.setLine(3, ChatColor.translateAlternateColorCodes('&', KitMaster.config().getString("kitSelectionSignText.line_4")).replace("%kit%", kit.name));
			player.sendMessage(MessageHandler.getMessage("make_sign.success", kit));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakKitSelectionSigns(BlockBreakEvent event) {
		if (!event.isCancelled() && SignHandler.isKitSign(event.getBlock().getLocation())) {
			Player player = event.getPlayer();
			if (!player.hasPermission("kitmaster.createsign")) {
				event.setCancelled(true);
				player.sendMessage(MessageHandler.getMessage("break_sign.fail_perms"));
				return;
			}
			SignHandler.removeKitSign(event.getBlock().getLocation());
			event.getPlayer().sendMessage(MessageHandler.getMessage("break_sign.success"));
		}
	}
	
	@EventHandler
	public void giveKitsOnRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		Kit respawnKit = null;
		for (Kit kit : KitHandler.getKits())
			if (player.isPermissionSet("kitmaster.respawn." + kit.name) && player.hasPermission("kitmaster.respawn." + kit.name)) {
				respawnKit = kit.applyParentAttributes();
				break;
			}
		if (respawnKit != null) {
			final Kit fRespawnKit = respawnKit.clone();
			Bukkit.getScheduler().scheduleSyncDelayedTask(KitMaster.plugin(), new Runnable() {
				public void run() {
					Actions.giveKit(player, fRespawnKit, GiveKitContext.PLUGIN_GIVEN_OVERRIDE);
				}
			});
		}
	}
	
	@EventHandler
	public void clearKitsWhenPlayerDies(PlayerDeathEvent event) {
		for (Kit kit : KitHandler.getKits())
			if (kit.booleanAttribute(Attribute.SINGLE_USE_LIFE)) {
				TimeStampHandler.clearTimeStamp(event.getEntity(), kit);
				if (kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT))
					TimeStampHandler.clearTimeStamp(null, kit);
			}
		if (KitMaster.config().getBoolean("clearKits.onDeath", true))
			Actions.clearAll(event.getEntity(), true, ClearKitsContext.PLAYER_DEATH);
	}
	
	@EventHandler
	public void removeItemDropsOnDeath(PlayerDeathEvent event) {
		for (Kit kit : HistoryHandler.getHistory(event.getEntity()))
			/*
			 * If the player has a kit that clears death drops
			 */
			if (kit.booleanAttribute(Attribute.RESTRICT_DEATH_DROPS)) {
				List<ItemStack> toRemove = new ArrayList<ItemStack>();
				/*
				 * Only look for similarity between drops and kit items, not
				 * quantity
				 */
				for (ItemStack drop : event.getDrops())
					for (ItemStack item : kit.items)
						if (ItemController.areSimilar(drop, item)) {
							toRemove.add(drop);
							break;
						}
				/*
				 * Remove drops that have been matched in the kit
				 */
				event.getDrops().removeAll(toRemove);
			}
	}
	
	@EventHandler
	public void clearKitsWhenPlayerQuits(PlayerQuitEvent event) {
		if (KitMaster.config().getBoolean("clearKits.onDisconnect", true))
			Actions.clearAll(event.getPlayer(), true, ClearKitsContext.PLAYER_DISCONNECT);
	}
	
	@EventHandler
	public void clearKitsWhenPlayerIsKicked(PlayerKickEvent event) {
		clearKitsWhenPlayerQuits(new PlayerQuitEvent(event.getPlayer(), "simulated"));
	}
	
	@EventHandler
	public void sendUpdateMessages(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("kitmaster.*") && KitMaster.isUpdateEnabled()) {
			switch (KitMaster.getUpdate().getResult()) {
				case FAIL_BADID:
				case FAIL_NOVERSION:
					player.sendMessage(MessageHandler.getMessage("update.fail_meta"));
					break;
				case FAIL_DBO:
					player.sendMessage(MessageHandler.getMessage("update.fail_connect"));
					break;
				case FAIL_DOWNLOAD:
					player.sendMessage(MessageHandler.getMessage("update.fail_download"));
					break;
				case SUCCESS:
					player.sendMessage(MessageHandler.getMessage("update.success"));
					break;
				case UPDATE_AVAILABLE:
					player.sendMessage(MessageHandler.getMessage("update.update_available"));
					break;
				default:
			}
		}
	}
	
	@EventHandler
	public void optionalShortcutKitCommands(PlayerCommandPreprocessEvent event) {
		if (KitMaster.config().getBoolean("shortcutKitCommands")) {
			Kit target = KitHandler.getKit(event.getMessage().replace("/", ""));
			if (target != null && target.name.equalsIgnoreCase(event.getMessage().replace("/", "")))
				event.setMessage((event.getMessage().contains("/") ? "/" : "") + "kit " + target.name);
		}
	}
	
	@EventHandler
	public void restrictArmorRemoval(InventoryClickEvent event) {
		/*
		 * If an armor slot was clicked
		 */
		if (event.getSlotType() == SlotType.ARMOR)
			for (Kit kit : HistoryHandler.getHistory((Player) event.getWhoClicked()))
				/*
				 * If the player has a kit that restricts armor
				 */
				if (kit.booleanAttribute(Attribute.RESTRICT_ARMOR))
					for (ItemStack item : kit.items)
						/*
						 * If that kit contains the armor being removed
						 */
						if (ItemController.areSimilar(item, event.getCurrentItem()))
							event.setCancelled(true);
	}
	
	@EventHandler
	public void restrictItemDrops(PlayerDropItemEvent event) {
		for (Kit kit : HistoryHandler.getHistory(event.getPlayer()))
			/*
			 * If the player has a kit that restricts drops
			 */
			if (kit.booleanAttribute(Attribute.RESTRICT_DROPS))
				for (ItemStack item : kit.items)
					/*
					 * If that kit contains the item being dropped
					 */
					if (ItemController.areSimilar(item, event.getItemDrop().getItemStack()))
						event.setCancelled(true);
	}
	
	@EventHandler
	public void restrictItemPickups(PlayerPickupItemEvent event) {
		for (Kit kit : HistoryHandler.getHistory(event.getPlayer()))
			/*
			 * If the player has a kit that restricts pickups
			 */
			if (kit.booleanAttribute(Attribute.RESTRICT_PICKUPS)) {
				/*
				 * By default cancel it
				 */
				event.setCancelled(true);
				for (ItemStack item : kit.items)
					/*
					 * If the kit contains that item, allow it
					 */
					if (item != null && ItemController.areSimilar(item, event.getItem().getItemStack())) {
						event.setCancelled(false);
						return;
					}
			}
	}
	
}
