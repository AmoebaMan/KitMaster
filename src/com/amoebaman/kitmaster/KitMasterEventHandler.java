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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.amoebaman.kitmaster.enums.GiveKitContext;
import com.amoebaman.kitmaster.enums.GiveKitResult;
import com.amoebaman.kitmaster.handlers.KitHandler;
import com.amoebaman.kitmaster.handlers.SignHandler;
import com.amoebaman.kitmaster.objects.Kit;

public class KitMasterEventHandler implements Listener{
	
	public static void registerEvents(KitMaster plugin){
		Bukkit.getPluginManager().registerEvents(new KitMasterEventHandler(), plugin);
	}

	/**
	 * Listens for players taking kits from kit selection signs
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockDamage(BlockDamageEvent event){
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

	/**
	 * Listens for kit selection signs being created
	 */
	@EventHandler
	public void onSignChange(SignChangeEvent event){
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

	/**
	 * Listens for kit selection signs being destroyed
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event){
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
	
	/**
	 * Listens for players respawning and handles respawn kits
	 */
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
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

	/**
	 * Listens for players dying and clears their kits if configured as such
	 */
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		if(KitMaster.config().getBoolean("clearKits.onDeath", true))
			KitMaster.clearAll(event.getEntity());
	}

	/**
	 * Listens for players disconnecting and clears their kits if configured as such
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if(KitMaster.config().getBoolean("clearKits.onDisconnect", true))
			KitMaster.clearAll(event.getPlayer());
	}
	
	/**
	 * Passes players being kicked to be handled like players quitting
	 */
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event){
		onPlayerQuit(new PlayerQuitEvent(event.getPlayer(), "simulated"));
	}

}
