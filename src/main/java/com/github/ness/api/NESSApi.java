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
}
