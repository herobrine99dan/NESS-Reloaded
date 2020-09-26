package com.github.ness.api;

import java.util.Collection;
import java.util.UUID;

/**
 * Manager of {@link AnticheatPlayer}s
 * 
 * @author A248
 *
 */
public interface PlayersManager {

	/**
	 * Gets an immutable copy of all anticheat players
	 * 
	 * @return an immutable copy of all anticheat players
	 */
	Collection<? extends AnticheatPlayer> getAllPlayers();

	/**
	 * Gets an anticheat player by UUID, or {@code null} if there is none
	 * 
	 * @param UUID the player UUID
	 * @return an anticheat player with the UUID, or {@code null} for none
	 */
	AnticheatPlayer getPlayer(UUID uuid);
	
}
