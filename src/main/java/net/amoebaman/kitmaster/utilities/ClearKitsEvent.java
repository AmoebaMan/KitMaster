package net.amoebaman.kitmaster.utilities;

import net.amoebaman.kitmaster.enums.ClearKitsContext;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * An event that is called whenever a kit is given to a player.
 * @author Dennison
 */
public class ClearKitsEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private boolean inventory, effects, permissions, heroes, cancelled;
	private ClearKitsContext context;
	
	/**
	 * Do not construct this event.  All event firing/handling is done by KitMaster.
	 */
	public ClearKitsEvent(Player player, boolean inventory, boolean effects, boolean permissions, boolean heroes, ClearKitsContext context){
		this.player = player;
		this.inventory = inventory;
		this.effects = effects;
		this.permissions = permissions;
		this.heroes = heroes;
		this.context = context;
		this.cancelled = false;
	}
	
	/**
	 * Do not construct this event.  All event firing/handling is done by KitMaster.
	 */
	public ClearKitsEvent(Player player, ClearKitsContext context){
		this.player = player;
		this.inventory = true;
		this.effects = true;
		this.permissions = true;
		this.heroes = true;
		this.context = context;
		this.cancelled = false;
	}
	
	/**
	 * Gets the player involved in this event.
	 * @return The player.
	 */
	public Player getPlayer(){ return player; }
	
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	/**
	 * Calls this event, sending it through the Bukkit event system and giving other plugins the chance to modify it.
	 */
	public void callEvent(){
		Bukkit.getServer().getPluginManager().callEvent(this);
	}
	
	public boolean isCancelled(){ return cancelled; }
	public void setCancelled(boolean cancel){ this.cancelled = cancel; }
	
	public boolean clearsInventory() { return inventory; }
	public void setClearsInventory(boolean inventory) { this.inventory = inventory; }
	
	public boolean clearsEffects() { return effects; }
	public void setClearsEffects(boolean effects) { this.effects = effects; }
	
	public boolean clearsPermissions() { return permissions; }
	public void setClearsPermissions(boolean permissions) { this.permissions = permissions; }
	
	public boolean clearsHeroes(){ return heroes; }
	public void setClearsHeroes(boolean heroes){ this.heroes = heroes; }
	
	public boolean clearsAll(){ return inventory && effects && permissions && heroes; }
	public void setClearsAll(boolean value){
		inventory = value;
		effects = value;
		permissions = value;
	}
	
	public ClearKitsContext getContext(){ return context; }
	
}
