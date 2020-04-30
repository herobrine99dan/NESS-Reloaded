package com.github.ness;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class PlayerManager {
	
	private final ConcurrentHashMap<UUID, NessPlayer> players = new ConcurrentHashMap<>();
	
	/**
	 * Gets a NessPlayer or creates one if it does not exist
	 * 
	 * @param player the corresponding player
	 * @return the ness player
	 */
	public NessPlayer getPlayer(Player player) {
		return players.computeIfAbsent(player.getUniqueId(), (k) -> new NessPlayer(player));
	}
	
}
