package com.github.ness.api;

import org.bukkit.entity.Player;

/**
 * Officially supported NESS API.
 *
 * @author A248
 */
public interface NESSApi {

	/**
	 * Adds an action to run when a player is detected for a cheat. <br>
	 * See {@link ViolationAction}
	 *
	 * @param action the violation action
	 * @deprecated Use {@link #addViolationTrigger(ViolationTrigger)} and {@link ViolationTrigger}
	 */
	@Deprecated
	void addViolationAction(@SuppressWarnings("deprecation") ViolationAction action);

	/**
	 * Adds a violation trigger which is run when a player is detected for a cheat
	 * 
	 * @param trigger the trigger to add
	 * @throws NullPointerException if {@code trigger} is null
	 */
	void addViolationTrigger(ViolationTrigger trigger);

	/**
	 * Flag a player for a cheat
	 *
	 * @param violation the cause
	 * @param player    the player to flag
	 */
	void flagHack(Violation violation, Player player);

}
