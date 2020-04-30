package com.github.ness.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MovementListener implements Listener {

	private final CheckManager manager;
	
	@EventHandler
	private void onPlayerMove(PlayerMoveEvent evt) {
		
	}
	
}
