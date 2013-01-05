package com.amoebaman.kitmaster;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.amoebaman.kitmaster.enums.Attribute;
import com.amoebaman.kitmaster.enums.GiveKitContext;
import com.amoebaman.kitmaster.enums.GiveKitResult;
import com.amoebaman.kitmaster.handlers.HistoryHandler;
import com.amoebaman.kitmaster.handlers.KitHandler;
import com.amoebaman.kitmaster.handlers.SignHandler;
import com.amoebaman.kitmaster.handlers.TimeStampHandler;
import com.amoebaman.kitmaster.objects.Kit;

public class KitMasterEventHandler implements Listener{
	
	public static void init(KitMaster plugin){
		Bukkit.getPluginManager().registerEvents(new KitMasterEventHandler(), plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void kitSelectionFromSigns(BlockDamageEvent event){
		Kit kit = SignHandler.getKitSign(event.getBlock().getLocation());
		if(kit != null){
			if(event.getPlayer().hasPermission("kitmaster.sign")){
				GiveKitResult result = KitMaster.giveKit(event.getPlayer(), kit, GiveKitContext.SIGN_TAKEN);
				if(KitMaster.DEBUG_KITS)
					KitMaster.logger().info("Result: " + result.name());
			}
			else
				event.getPlayer().sendMessage(ChatColor.ITALIC + "You don't have permission to take kits from signs");
		}
	}

	@EventHandler
	public void createKitSelectionSigns(SignChangeEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if(event.getLine(0).equalsIgnoreCase("kit")){
			if(!player.hasPermission("kitmaster.createsign")){
				block.breakNaturally();
				player.sendMessage(ChatColor.ITALIC + "You do not have permission to create kit selection signs");
				return;
			}
			if(!KitHandler.isKit(event.getLine(1))){
				block.breakNaturally();
				player.sendMessage(ChatColor.ITALIC + "That kit does not exist");
				return;
			}
			Kit kit = KitHandler.getKit(event.getLine(1));
			SignHandler.saveKitSign(kit, event.getBlock().getLocation());
			event.setLine(0,	"==============");
			event.setLine(1, ChatColor.BOLD + "[Kit Select]");
			event.setLine(2, ChatColor.ITALIC + kit.name);
			event.setLine(3, "==============");
			player.sendMessage(ChatColor.ITALIC + "Kit select sign registered");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void breakKitSelectionSigns(BlockBreakEvent event){
		if(!event.isCancelled() && SignHandler.isKitSign(event.getBlock().getLocation())){
			Player player = event.getPlayer();
			if(!player.hasPermission("kitmaster.createsign")){
				event.setCancelled(true);
				player.sendMessage(ChatColor.ITALIC + "You do not have permission to break kit selection signs");
				return;
			}
			SignHandler.removeKitSign(event.getBlock().getLocation());
			event.getPlayer().sendMessage(ChatColor.ITALIC + "Kit select sign unregistered");
		}
	}
	
	@EventHandler
	public void giveKitsOnRespawn(PlayerRespawnEvent event){
		final Player player = event.getPlayer();
		Kit respawnKit = null;
		for(Kit kit : KitHandler.getKits())
			if(player.hasPermission("kitmaster.respawn." + kit.name)){
				respawnKit = kit.applyParent();
				break;
			}
		if(respawnKit != null){
			final Kit fRespawnKit = respawnKit.clone();
			Bukkit.getScheduler().scheduleSyncDelayedTask(KitMaster.plugin(), new Runnable(){ public void run(){
				KitMaster.giveKit(player, fRespawnKit, GiveKitContext.PLUGIN_GIVEN_OVERRIDE);
			}});
		}
	}

	@EventHandler
	public void clearKitsWhenPlayerDies(PlayerDeathEvent event){
		for(Kit kit : HistoryHandler.getHistory(event.getEntity()))
			if(kit.booleanAttribute(Attribute.SINGLE_USE_LIFE)){
				TimeStampHandler.clearTimeStamp(event.getEntity(), kit);
				if(kit.booleanAttribute(Attribute.GLOBAL_TIMEOUT))
					TimeStampHandler.clearTimeStamp(null, kit);
			}
		if(KitMaster.config().getBoolean("clearKits.onDeath", true))
			KitMaster.clearAll(event.getEntity());
	}
	
	@EventHandler
	public void removeItemDropsOnDeath(PlayerDeathEvent event){
		for(Kit kit : HistoryHandler.getHistory(event.getEntity()))
			if(kit.booleanAttribute(Attribute.RESTRICT_DEATH_DROPS))
				event.getDrops().clear();
	}
	
	@EventHandler
	public void clearKitsWhenPlayerQuits(PlayerQuitEvent event){
		if(KitMaster.config().getBoolean("clearKits.onDisconnect", true))
			KitMaster.clearAll(event.getPlayer());
	}
	
	@EventHandler
	public void clearKitsWhenPlayerIsKicked(PlayerKickEvent event){
		clearKitsWhenPlayerQuits(new PlayerQuitEvent(event.getPlayer(), "simulated"));
	}
	
	@EventHandler
	public void sendUpdateMessages(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("kitmaster.*")){
			switch(KitMaster.update.getResult()){
			case FAIL_BADSLUG:
			case FAIL_NOVERSION:
				player.sendMessage(ChatColor.ITALIC + "KitMaster: Failed to check for updates due to bad code, contact the developer immediately"); break;
			case FAIL_DBO:
				player.sendMessage(ChatColor.ITALIC + "KitMaster: Failed to connect to BukkitDev while trying to check for updates"); break;
			case FAIL_DOWNLOAD:
				player.sendMessage(ChatColor.ITALIC + "KitMaster: Failed to download an update from BukkitDev"); break;
			case SUCCESS:
				player.sendMessage(ChatColor.ITALIC + ""); break;
			case UPDATE_AVAILABLE:
				player.sendMessage(ChatColor.ITALIC + "KitMaster: Version " + KitMaster.update.getLatestVersionString().replace("v", "") + " is available on BukkitDev, you currently have version " + KitMaster.plugin().getDescription().getVersion()); break;
			default: }
		}
	}
	
	@EventHandler
	public void restrictArmorRemoval(InventoryClickEvent event){
		for(Kit kit : HistoryHandler.getHistory((Player) event.getWhoClicked()))
			if(event.getSlotType() == SlotType.ARMOR && kit.booleanAttribute(Attribute.RESTRICT_ARMOR))
				event.setCancelled(true);
	}
	
	@EventHandler
	public void restrictItemDrops(PlayerDropItemEvent event){
		for(Kit kit : HistoryHandler.getHistory(event.getPlayer()))
			if(kit.booleanAttribute(Attribute.RESTRICT_DROPS))
				event.setCancelled(true);
	}
	
	@EventHandler
	public void restrictItemPickups(PlayerPickupItemEvent event){
		for(Kit kit : HistoryHandler.getHistory(event.getPlayer()))
			if(kit.booleanAttribute(Attribute.RESTRICT_PICKUPS))
				event.setCancelled(true);
	}

}
