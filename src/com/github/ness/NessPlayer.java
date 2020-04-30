package com.github.ness;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class NessPlayer {

	private final Player player;
	
	private int suspicion;
	
	private Set<Long> clickHistory = ConcurrentHashMap.newKeySet();
	
	public NessPlayer(Player player) {
		this.player = player;
	}
	
	public void cps_Click() {
		clickHistory.add(System.currentTimeMillis());
	}
	
}
