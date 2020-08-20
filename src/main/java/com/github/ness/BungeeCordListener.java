package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeCordListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		final String subchannel = in.readUTF();
		if (subchannel.equals("NESS-Reloaded")) {
			final String notify = in.readUTF();
			for (Player staff : Bukkit.getOnlinePlayers()) {
				if (staff.hasPermission("ness.notify") || staff.hasPermission("ness.notify.hacks")) {
					staff.sendMessage(notify);
				}
			}
		}
	}

}
