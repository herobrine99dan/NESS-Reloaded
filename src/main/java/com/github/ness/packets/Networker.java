package com.github.ness.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Networker implements AutoCloseable {

	private final JavaPlugin plugin;
	private final PacketListener listener;

	public Networker(JavaPlugin plugin, PacketListener listener) {
		this.plugin = plugin;
		this.listener = listener;
	}

	public void start() {
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			listener.injectPlayer(player);
		}
	}

	@Override
	public void close() {
		HandlerList.unregisterAll(listener);
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			listener.removePlayer(player);
		}
	}

}
