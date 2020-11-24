package com.github.ness.check;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CoreListener implements Listener {
	
	CheckManager manager;
	
	public CoreListener(CheckManager manager) {
		this.manager = manager;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerQuitEvent e) {
		
	}


}
