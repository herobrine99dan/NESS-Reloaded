package com.github.ness.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeCordListener implements PluginMessageListener {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

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
