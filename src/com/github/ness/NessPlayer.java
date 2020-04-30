package com.github.ness;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.github.ness.annotation.SyncOnly;

import lombok.Getter;

public class NessPlayer {

	@SyncOnly
	private final Player player;
	
	@Getter
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();
	
	public NessPlayer(Player player) {
		this.player = player;
	}
	
}
