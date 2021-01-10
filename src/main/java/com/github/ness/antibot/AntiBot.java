package com.github.ness.antibot;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;

public class AntiBot {

	private final JavaPlugin plugin;
	private final AntiBotConfig config;
	private final int maxPlayersPerSecond;
	private final String kickMessage;
	
	private final Cache<UUID, Boolean> whitelist;
	private final AtomicLong counter = new AtomicLong();

	public AntiBot(JavaPlugin plugin, AntiBotConfig config) {
		this.plugin = plugin;
		this.config = config;

		maxPlayersPerSecond = config.maxPlayersPerSecond();
		kickMessage = ChatColor.translateAlternateColorCodes('&', config.kickMessage());

		whitelist = Caffeine.newBuilder().maximumSize(1200L).expireAfterAccess(Duration.ofDays(4L))
				.scheduler(Scheduler.systemScheduler()).build();
	}
	
	public void initiate() {
		plugin.getServer().getPluginManager().registerEvents(new ListenerImpl(), plugin);
		plugin.getServer().getScheduler().runTaskTimer(plugin, () -> counter.set(0L), 0L, 20L);
	}
	
	/*
	 * This class being public improves some of Paper's optimisations with event handlers
	 */
	public class ListenerImpl implements Listener {

		@EventHandler(priority = EventPriority.LOWEST)
		public void interceptLogins(AsyncPlayerPreLoginEvent event) {
			if (counter.incrementAndGet() > maxPlayersPerSecond
					&& whitelist.getIfPresent(event.getUniqueId()) == null) {
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
			}
		}

		@EventHandler
		public void onJoin(PlayerJoinEvent e) {
			Player player = e.getPlayer();
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

				if (player.isOnline()) {
					whitelist.put(player.getUniqueId(), Boolean.TRUE);
				}
			}, config.timeUntilTrusted() * 20L);
		}
	}

}
