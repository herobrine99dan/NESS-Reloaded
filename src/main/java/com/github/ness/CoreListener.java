package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;

public class CoreListener implements Listener {

	private final CheckManager manager;
	
	CoreListener(CheckManager manager) {
		this.manager = manager;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		manager.getPlayer(event.getPlayer()).actionTime.put("onJoin", System.nanoTime() / 1000_000L);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent event) {
		Location destination = event.getTo();
		if (destination == null) {
			return;
		}
		String destinationWorld = destination.getWorld().getName();
		Location source = event.getFrom();
		String sourceWorld = source.getWorld().getName();
		if (!destinationWorld.equals(sourceWorld)) {
			return;
		}
		Player player = event.getPlayer();
		MovementValues values = new MovementValues(player, ImmutableLoc.of(destination, destinationWorld),
				ImmutableLoc.of(source, sourceWorld));
		manager.getPlayer(player).updateMovementValue(values);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent evt) {
		Player player = evt.getPlayer();
		final long tenSecondsLater = 20L * 10L;
		Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
			if (player.isOnline()) {
				manager.removePlayer(player);
			}
		}, tenSecondsLater);
	}
	
}
