package com.github.ness;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CheckManager {
	
	private final ConcurrentHashMap<UUID, NessPlayer> players = new ConcurrentHashMap<>();
	
	void runAll() {
		
	}
	
	/**
	 * Gets a NessPlayer or creates one if it does not exist
	 * 
	 * @param uuid the corresponding player uuid
	 * @return the ness player
	 */
	public NessPlayer getPlayer(UUID uuid) {
		return players.computeIfAbsent(uuid, (k) -> new NessPlayer());
	}
	
}
