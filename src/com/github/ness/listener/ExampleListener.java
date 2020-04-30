package com.github.ness.listener;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NESSAnticheat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExampleListener {
	
	private final NESSAnticheat ness;
	
	private void preAsyncMoveEvent(PlayerMoveEvent evt) {
		Location from = evt.getFrom();
		Location to = evt.getTo();
		// do calculations
	}
	
	private void withAsyncMoveEvent(PlayerMoveEvent evt) {
		Location from = evt.getFrom();
		Location to = evt.getTo();
		ness.getExecutor().execute(() -> {
			// do calculations
		});
	}
	
}
