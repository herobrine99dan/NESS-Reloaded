package com.github.ness.api.example;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.NESSApi;
import com.github.ness.api.PlayerFlagEvent;

public class NESSExample extends JavaPlugin implements Listener {
	
	private NESSApi api;

	@Override
	public void onEnable() {
		api = getServer().getServicesManager().load(NESSApi.class);

		Bukkit.getPluginManager().registerEvents(this, this);
		api.addViolationTrigger(new CheaterBurner());
	}

	@EventHandler
	public void onViolation(PlayerFlagEvent evt) {

		// NESS has bypass permissions built-in and better integrated
		// This is just an example

		AnticheatPlayer anticheatPlayer = evt.getPlayer();
		String permission = "anticheat.bypass." + evt.getCheck().getCheckName();

		boolean hasBypassPermission;
		if (evt.isAsynchronous()) {
			// hasPermission is not necessarily thread safe
			// this would need to either resynchronise or detect thread-safe permissions plugins
			hasBypassPermission = false;
		} else {
			hasBypassPermission = anticheatPlayer.getPlayer().hasPermission(permission);
		}
		if (hasBypassPermission) {
			evt.setCancelled(true);
		}
	}

}
