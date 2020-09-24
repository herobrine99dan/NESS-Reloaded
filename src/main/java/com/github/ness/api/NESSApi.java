package com.github.ness.api;

import java.util.Collection;
import java.util.UUID;

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
	 * Gets an immutable copy of all anticheat checks
	 * 
	 * @return an immutable collection copy of all anticheat checks
	 */
	Collection<? extends AnticheatCheck> getAllChecks();
	
	/**
	 * Gets an immutable copy of all anticheat players
	 * 
	 * @return an immutable copy of all anticheat players
	 */
	Collection<? extends AnticheatPlayer> getAllPlayers();

	/**
	 * Gets an anticheat player by UUID
	 * 
	 * @param UUID the player UUID
	 * @return an anticheat player with the UUID
	 */
	AnticheatPlayer getPlayer(UUID uuid);

	/**
	 * Flag a player for a cheat
	 *
	 * @param violation the cause
	 * @param player    the player to flag
	 * @deprecated Use {@link AnticheatCheck#flagHack(AnticheatPlayer)}
	 */
	@Deprecated
	void flagHack(@SuppressWarnings("deprecation") Violation violation, Player player);

}
