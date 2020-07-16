package com.github.ness.api;

import org.bukkit.entity.Player;

/**
 * Officially supported NESS API.
 * 
 * @author A248
 *
 */
public interface NESSApi {

	/**
	 * Adds an action to run when a player is detected for a cheat. <br>
	 * See {@link ViolationAction}
	 * 
	 * @param action the violation action
	 */
	void addViolationAction(ViolationAction action);
	/**
	 * Flag a player for a cheat
	 * 
	 * @param violation the cause
	 * @param player the player to flag
	 */
	void flagHack(Violation violation, Player player);
	
}
