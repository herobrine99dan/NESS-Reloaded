package com.github.ness;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.github.ness.annotation.SyncOnly;

import lombok.Getter;
import lombok.Setter;

public class NessPlayer implements AutoCloseable {

	@Getter
	@Setter
	private volatile Violation violation;
	
	@SyncOnly
	private final Player player;
	
	@Getter
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();
	
	public NessPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void close() {
		
	}
	
	
	
}
