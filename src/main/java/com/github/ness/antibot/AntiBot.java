package com.github.ness.antibot;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiBot implements Listener, Runnable {

	private final JavaPlugin plugin;
	private final AntiBotConfig config;
	
	private final Set<String> whitelistbypass = new HashSet<String>();
	private int playerCounter = 0;

	public AntiBot(JavaPlugin plugin, AntiBotConfig config) {
		this.plugin = plugin;
		this.config = config;
	}

	@EventHandler
	public void Check(AsyncPlayerPreLoginEvent e) {
		playerCounter++;
		if (playerCounter > config.maxPlayersPerSecond() && !whitelistbypass.contains(e.getName())) {
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
					ChatColor.translateAlternateColorCodes('&', config.kickMessage()));
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
		}.runTaskLater(plugin, config.timeUntilTrusted() * 20L);
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
