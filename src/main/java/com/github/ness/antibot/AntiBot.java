package com.github.ness.antibot;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ness.NESSAnticheat;

public class AntiBot implements Listener, Runnable {

	Set<String> whitelistbypass = new HashSet<String>();
	int neededSeconds = 10;
	NESSAnticheat ness;
	int maxPlayers;
	int playerCounter = 0;
	String message;

	public AntiBot(NESSAnticheat ness) {
		this.ness = ness;
		ConfigurationSection config = this.ness.getNessConfig().getConfig().getConfigurationSection("antibot");
		this.neededSeconds = config.getInt("minimumseconds", 10);
		message = config.getString("message", "BotAttack Detected! By NESS Reloaded");
		maxPlayers = config.getInt("maxplayers", 15);
	}

	@EventHandler
	public void Check(AsyncPlayerPreLoginEvent e) {
		playerCounter++;
		if (playerCounter > maxPlayers && !whitelistbypass.contains(e.getName())) {
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, message);
		} else {
			e.allow();
		}
	}

	@EventHandler
	public void Check(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (p.isOnline()) {
					whitelistbypass.add(p.getName());
				}
			}
		}.runTaskLater(this.ness, neededSeconds * 20L);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                if (p.getName().equalsIgnoreCase(player.getName()) || player.getName().equalsIgnoreCase(p.getName())) {
                    p.kickPlayer("You are already joined!");
                }
            }
        }
	}

	@Override
	public void run() {
		this.playerCounter = 0;
	}

}
