package com.github.ness.discord;

import java.awt.Color;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ness.NESSAnticheat;

public class DiscordHackWarn {

	public static void webHookSender(Player hacker, String hack, int level, String module) {
		String webhookurl = NESSAnticheat.getInstance().getNessConfig().getWebHook();
		if (webhookurl == null || webhookurl.isEmpty()) {
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				DiscordWebhook webhook = new DiscordWebhook(webhookurl);
				webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Anti-Cheat")
						.setDescription("hacker maybe is cheating!".replace("hacker", hacker.getName()))
						.setColor(Color.RED).addField("Cheater", hacker.getName(), true)
						.addField("Cheat", hack + "(module)".replace("module", module), true)
						.addField("VL", Integer.toString(level), false));
				// webhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription("Player
				// hacker seems to be use cheat(module)".replace("cheat", hack)
				// .replace("module", module).replace("hacker", hacker.getName())));
				try {
					webhook.execute();
				} catch (IOException e) {

				}
			}
		}.runTaskAsynchronously(NESSAnticheat.getInstance());
	}
}
