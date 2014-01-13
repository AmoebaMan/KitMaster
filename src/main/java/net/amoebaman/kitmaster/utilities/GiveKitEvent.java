package net.amoebaman.kitmaster.utilities;

import net.amoebaman.kitmaster.enums.GiveKitContext;
import net.amoebaman.kitmaster.objects.Kit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * An event that is called whenever a kit is given to a player.
 * @author Dennison
 */
public class GiveKitEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Kit kit;
	private GiveKitContext context;
	private boolean cancelled;
 
	/**
	 * Do not construct this event.  All event firing/handling is done by KitMaster.
	 */
    public GiveKitEvent(Player player, Kit kit, GiveKitContext context){
		this.player = player;
		this.kit = kit;
		this.context = context;
		this.cancelled = false;
    }
    
    /**
     * Gets the player involved in this event.
     * @return The player.
     */
    public Player getPlayer(){ return player; }
    
    /**
     * Gets the kit involved in this event.
     * @return The kit.
     */
    public Kit getKit(){ return kit; }
    
    /**
     * Gets the context/reason for this kit being given.
     * @return The context.
     */
    public GiveKitContext getContext(){ return context; }
    
    /**
     * Replaces the kit to be given with a new or modified one.
     * @param newKit The new kit that will be given.
     */
    public void setKit(Kit newKit){ kit = newKit; }
  
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
    
    /**
     * Calls this event, sending it through the Bukkit event system and giving other plugins the chance to modify it.
     */
    public void callEvent(){
    	Bukkit.getServer().getPluginManager().callEvent(this);
    	if(context == GiveKitContext.COMMAND_GIVEN)
    		cancelled = false;
    }
	
	public boolean isCancelled(){ return cancelled; }
    public void setCancelled(boolean cancel){ this.cancelled = cancel; }
	
}
