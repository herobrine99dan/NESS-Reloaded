package com.github.ness.listener;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessScheduler;

public class ExampleListener {

	private NessScheduler scheduler;
	
	private void preAsyncMoveEvent(PlayerMoveEvent evt) {
		Location from = evt.getFrom();
		Location to = evt.getTo();
		// do calculations
	}
	
	private void withAsyncMoveEvent(PlayerMoveEvent evt) {
		Location from = evt.getFrom();
		Location to = evt.getTo();
		scheduler.execute(() -> {
			// do calculations
		});
	}
	
}
