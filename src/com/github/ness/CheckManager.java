package com.github.ness;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.github.ness.check.AbstractCheck;

public class CheckManager {
	
	private final ConcurrentHashMap<UUID, NessPlayer> players = new ConcurrentHashMap<>();
	
	private final Set<AbstractCheck<?>> checks = new HashSet<>();
	
	private final NESSAnticheat ness;
	
	CheckManager(NESSAnticheat ness) {
		this.ness = ness;
	}
	
	public Executor getExecutor() {
		return ness.getExecutor();
	}
	
	void onAnyEvent(Event evt) {
		checks.forEach((check) -> check.checkAnyEvent(evt));
	}
	
	void startAsyncTimer() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(ness, () -> {
			checks.forEach((check) -> {
				if (check.canCheckAsyncPeriodic()) {
					players.values().forEach((player) -> {
						check.checkAsyncPeriodic(player);
					});
				}
			});
		}, 5L, 1L);
	}
	
	public void forEachPlayer(Consumer<NessPlayer> action) {
		players.values().forEach(action);
	}
	
	void registerListener() {
		Bukkit.getPluginManager().registerEvents(new EntiretyListener(this), ness);
	}

	void unregisterListeners() {
		HandlerList.unregisterAll(ness);
	}
	
	/**
	 * Gets a NessPlayer or creates one if it does not exist
	 * 
	 * @param player the corresponding player
	 * @return the ness player
	 */
	public NessPlayer getPlayer(Player player) {
		return players.get(player.getUniqueId());
	}
	
	void addPlayer(Player player) {
		players.put(player.getUniqueId(), new NessPlayer(player));
	}
	
	void removePlayer(Player player) {
		players.remove(player.getUniqueId());
	}
	
}
