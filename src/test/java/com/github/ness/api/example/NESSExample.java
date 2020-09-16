package com.github.ness.api.example;

import com.github.ness.NESSPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NESSAnticheat;
import com.github.ness.api.NESSApiImpl;
import com.github.ness.api.NESSApi;
import com.github.ness.api.impl.PlayerViolationEvent;
import com.github.ness.api.Violation;

public class NESSExample extends JavaPlugin implements Listener {
	NESSApi api;

	public void onEnable() {
		api = new NESSApiImpl(NESSAnticheat.getInstance());
		Bukkit.getPluginManager().registerEvents(this, this);
		api.addViolationAction(new CheaterBurner());
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (e.getTo().distanceSquared(e.getFrom()) > 1) {
			api.flagHack(new Violation("Speed", "HighDistance"), e.getPlayer());
		}
	}

	@EventHandler
	public void onViolation(PlayerViolationEvent e) {
		Violation violation = e.getViolation();
		String cheat = violation.getCheck();
		String module = violation.getDetails();
		int vl = e.getViolations();
		e.setCancelled(true);
		NESSPlayer np = e.getNessplayer();
		Player p = np.getPlayer();
	}

}
