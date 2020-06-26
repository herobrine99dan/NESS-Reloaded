package com.github.ness.packets;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class PlayerPacketsData {

	private static Map<String, PlayerPacketsData> nessplayers = new HashMap<String, PlayerPacketsData>();
	public int balance;
	public long lastTime;

	private PlayerPacketsData(Player player) {
		nessplayers.put(player.getName(), this);
	}

// Return a running instance (or create a new one)
	public static PlayerPacketsData getInstance(Player player) {
		if (nessplayers == null) {
			return new PlayerPacketsData(player);
		}
		if (!nessplayers.containsKey(player.getName())) {
			return new PlayerPacketsData(player);
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
