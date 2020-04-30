package com.github.ness;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.github.ness.annotation.SyncOnly;

public class NessPlayer {

	@SyncOnly
	private final Player player;
	
	private int suspicion;
	
	/**
	 * Retain clicks for 4 seconds in CPS check
	 * 
	 */
	private static final int CLICK_HISTORY_RETENTION = 4;
	private Set<Long> clickHistory = new HashSet<>();
	
	public NessPlayer(Player player) {
		this.player = player;
	}
	
	@SyncOnly
	public void checkCpsAndPossiblyKick() {
		long now = System.currentTimeMillis();
		clickHistory.add(now);
		clickHistory.removeIf((time) -> time - now > (CLICK_HISTORY_RETENTION * 1000L));
		int clicks = clickHistory.size();
		if (clicks / CLICK_HISTORY_RETENTION > 14) {
			player.kickPlayer("Failed CPS check! (CPS > 14)");
		}
	}
	
}
