package com.github.ness.antibot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.ness.NessAnticheat;

public class AntiBot {
    /**
     * This class is just a small AntiBot Protection using Async Events
     * @author herobrine99dan with some improvements of A248
     */

    private final NessAnticheat plugin;
    private final int maxPlayersPerSecond;
    private final String kickMessage;

    private final Set<UUID> whitelist;
    private final AtomicLong counter = new AtomicLong();
    private final int timeUntilTrusted;

    public AntiBot(NessAnticheat plugin) {
        this.plugin = plugin;
        ConfigurationSection section = plugin.getPlugin().getConfig().getConfigurationSection("antibot");
        maxPlayersPerSecond = section.getInt("max-players-per-second", 15);
        timeUntilTrusted = section.getInt("time-until-trusted", 10);
        kickMessage = ChatColor.translateAlternateColorCodes('&',
                section.getString("kick-message", "Bot Attack Detected! By NESS Reloaded"));

        whitelist = Collections.synchronizedSet(new HashSet<UUID>());
    }

    public void initiate() {
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerImpl(), plugin.getPlugin());
        plugin.getPlugin().getServer().getScheduler().runTaskTimer(plugin.getPlugin(), () -> counter.set(0L), 0L, 20L);
    }

    /*
     * This class being public improves some of Paper's optimisations with event
     * handlers
     */
    public class ListenerImpl implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void interceptLogins(AsyncPlayerPreLoginEvent event) {
            if (whitelist.size() > 120) {
                whitelist.remove(whitelist.iterator().next());
            }
            if (counter.incrementAndGet() > maxPlayersPerSecond && whitelist.contains(event.getUniqueId())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage);
            }
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            plugin.getPlugin().getServer().getScheduler().runTaskLater(plugin.getPlugin(), () -> {

                if (player.isOnline()) {
                    whitelist.add(player.getUniqueId());
                }
            }, timeUntilTrusted * 20L);
        }
    }

}
