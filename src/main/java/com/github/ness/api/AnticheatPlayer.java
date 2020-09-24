package com.github.ness.api;

import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * A player tracked by an anticheat
 * 
 * @author A248
 *
 */
public interface AnticheatPlayer {

	/**
	 * Provides thread safe access to the UUID of the targeted player
	 * 
	 * @return the UUID of the player tracked
	 */
	UUID getUniqueId();
	
	/**
	 * Gets the Bukkit {@code Player} tracked. May be computed on demand; not necessarily
	 * thread safe
	 * 
	 * @return the bukkit player
	 */
	Player getPlayer();
	
}
