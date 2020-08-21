package com.github.ness;

import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CoreListener implements Listener {

	private final CheckManager manager;
	
	CoreListener(CheckManager manager) {
		this.manager = manager;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMove(PlayerMoveEvent event) {
		if (event.getTo() == null || event.getFrom() == null || event.getPlayer() == null) {
			return;
		}
		if (event.getTo().getWorld().getName() != event.getFrom().getWorld().getName()) {
			return;
		}
		MovementValues values = new MovementValues(event.getPlayer(), Utility.locationToImmutableLoc(event.getTo().clone()),
				Utility.locationToImmutableLoc(event.getFrom().clone()));
		manager.getPlayer(event.getPlayer()).updateMovementValue(values);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent evt) {
		long tenSecondsLater = 20L * 10L;
		Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
			manager.removePlayer(evt.getPlayer());
		}, tenSecondsLater);
	}
	
}
