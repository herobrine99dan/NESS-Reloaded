package com.github.ness;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EntiretyListener implements Listener {

	private final CheckManager manager;
	
	@EventHandler
	private void onAnyEvent(Event evt) {
		if (evt instanceof PlayerJoinEvent) {
			manager.addPlayer(((PlayerJoinEvent) evt).getPlayer());
		} else if (evt instanceof PlayerQuitEvent) {
			manager.removePlayer(((PlayerQuitEvent) evt).getPlayer());
		} else {
			manager.onAnyEvent(evt);
		}
	}
	
}
