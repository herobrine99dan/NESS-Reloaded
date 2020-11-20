package com.github.ness.api;

import org.bukkit.entity.Player;

/**
 * Officially supported NESS API.
 *
 * @author A248
 */
public interface NESSApi {
	
	/**
	 * Gets the infraction manager
	 * 
	 * @return the infraction manager
	 */
	InfractionManager getInfractionManager();
	
	/**
	 * Gets the checks manager
	 * 
	 * @return the checks manager
	 */
	ChecksManager getChecksManager();
	
	/**
	 * Gets the players manager
	 * 
	 * @return the players manager
	 */
	PlayersManager getPlayersManager();
	
	/**
	 * Adds an action to run when a player is detected for a cheat. <br>
	 * See {@link ViolationAction}
	 *
	 * @param action the violation action
	 * @deprecated Use {@link InfractionManager#addTrigger(InfractionTrigger)} and {@link InfractionTrigger} instead
	 */
	@Deprecated
	void addViolationAction(@SuppressWarnings("deprecation") ViolationAction action);

	/**
	 * Flag a player for a cheat
	 *
	 * @param violation the cause
	 * @param player    the player to flag
	 * @deprecated Use {@link AnticheatCheck#flagHack(AnticheatPlayer)} instead
	 */
	@Deprecated
	void flagHack(@SuppressWarnings("deprecation") Violation violation, Player player);

}
