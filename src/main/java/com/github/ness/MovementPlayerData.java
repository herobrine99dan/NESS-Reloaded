package com.github.ness;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import lombok.Getter;

public class MovementPlayerData {
	@Getter
	private Player player;
	public long pingspooftimer;
	public long oldpingspooftimer;
	private static Map<String, MovementPlayerData> nessplayers = new HashMap<String, MovementPlayerData>();

	private MovementPlayerData(Player player) {
		this.player = player;
		oldpingspooftimer = 0;
		pingspooftimer = 0;
		nessplayers.put(player.getName(), this);
	}
	
	// Return a running instance (or create a new one)
	/**
	 * Return a running instance (or create a new one)
	 * 
	 * @param player the corresponding player
	 */
	public static MovementPlayerData getInstance(Player player) {
		if (nessplayers == null) {
			return new MovementPlayerData(player);
		}
		if (!nessplayers.containsKey(player.getName())) {
			return new MovementPlayerData(player);
		} else if (nessplayers.containsKey(player.getName())) {
			return nessplayers.get(player.getName());
		} else {
			return null;
		}
	}

	// Remove an Instance
	public static boolean removeInstance(Player player) {
		if (nessplayers.containsKey(player.getName())) {
			nessplayers.remove(player.getName());
			return true;
		} else {
			return false;
		}
	}

}