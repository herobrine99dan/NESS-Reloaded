package com.github.ness.api;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Cancellable event indicating a player has been flagged for a specific check
 * 
 * @author A248
 *
 */
public final class PlayerFlagEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final AnticheatPlayer player;
	private final AnticheatCheck check;
	
	private volatile boolean cancelled;
	
	/**
	 * Creates from a player and check
	 * 
	 * @param player the player
	 * @param check the check
	 */
	public PlayerFlagEvent(AnticheatPlayer player, AnticheatCheck check) {
		super(!Bukkit.isPrimaryThread());
		this.player = player;
		this.check = check;
	}
	
	/**
	 * Gets the anticheat player who was flagged for cheating
	 * 
	 * @return the player
	 */
	public AnticheatPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the check which detected the cheating
	 * 
	 * @return the check
	 */
	public AnticheatCheck getCheck() {
		return check;
	}
	
	/**
	 * Required handler list getter per Bukkit
	 * 
	 * @return the handlers for this event
	 */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
