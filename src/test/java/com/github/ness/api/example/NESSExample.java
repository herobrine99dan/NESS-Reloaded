package com.github.ness.api.example;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessPlayer;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.NESSApi;
import com.github.ness.api.PlayerFlagEvent;
import com.github.ness.api.Violation;
import com.github.ness.api.impl.PlayerPunishEvent;

public class NESSExample extends JavaPlugin implements Listener {
	
	private NESSApi api;

	@Override
	public void onEnable() {
		api = getServer().getServicesManager().load(NESSApi.class);

		Bukkit.getPluginManager().registerEvents(this, this);
		api.addViolationTrigger(new CheaterBurner());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().distanceSquared(e.getFrom()) > 1) {
			api.flagHack(new Violation("Speed", "HighDistance"), e.getPlayer());
		}
	}

	@EventHandler
	public void onViolation(PlayerFlagEvent evt) {
		String cheatName = evt.getCheck().getCheckName();

		evt.setCancelled(true);

		AnticheatPlayer anticheatPlayer = evt.getPlayer();
		Player player = anticheatPlayer.getPlayer();
	}

}
