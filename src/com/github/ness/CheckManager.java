package com.github.ness;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.ness.listener.DamageListener;
import com.github.ness.listener.InteractionListener;
import com.github.ness.listener.MovementListener;

public class CheckManager {
	
	private final ConcurrentHashMap<UUID, NessPlayer> players = new ConcurrentHashMap<>();
	
	private final NESSAnticheat ness;
	
	CheckManager(NESSAnticheat ness) {
		this.ness = ness;
	}
	
	public Executor getExecutor() {
		return ness.getExecutor();
	}
	
	void registerChecks() {
		Bukkit.getPluginManager().registerEvents(new InteractionListener(this), ness);
		Bukkit.getPluginManager().registerEvents(new DamageListener(this), ness);
		Bukkit.getPluginManager().registerEvents(new MovementListener(this), ness);
	}

	void unregisterChecks() {
		HandlerList.unregisterAll(ness);
	}
	
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
