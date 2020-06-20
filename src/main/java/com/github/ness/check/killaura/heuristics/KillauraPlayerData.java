package com.github.ness.check.killaura.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

public class KillauraPlayerData {
	
	private static Map<String, KillauraPlayerData> nessplayers = new HashMap<String, KillauraPlayerData>();
	private List<Double> angles;

	private KillauraPlayerData(Player player) {
		nessplayers.put(player.getName(), this);
		angles = new ArrayList<Double>();
	}

// Return a running instance (or create a new one)
	public static KillauraPlayerData getInstance(Player player) {
		if (nessplayers == null) {
			return new KillauraPlayerData(player);
		}
		if (!nessplayers.containsKey(player.getName())) {
			return new KillauraPlayerData(player);
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
	
	public void setAnglesList(List<Double> data) {
		angles = data;
	}
	
	public List<Double> getAnglesList(){
		return angles;
	}

}
