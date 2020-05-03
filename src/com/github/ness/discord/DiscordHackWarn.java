package com.github.ness.discord;

import java.awt.Color;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.MSG;
import org.mswsplex.MSWS.NESS.NESS;

public class DiscordHackWarn {

	public static void WebHookSender(Player hacker, String hack, int level, int identifier,
			String module) {
		String webhookurl = NESS.main.config.getString("Configuration.WebHookURL");
		if (webhookurl == null || webhookurl == "") {
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				DiscordWebhook webhook = new DiscordWebhook(webhookurl);
				webhook.addEmbed(
						new DiscordWebhook.EmbedObject().setTitle("Anti-Cheat").setDescription("hacker maybe is cheating!".replace("hacker", hacker.getName()))
								.setColor(Color.RED).addField("Cheater", hacker.getName(), true)
								.addField("Cheat",
										hack + "(module-identifier)".replace("module", module).replace("identifier",
												Integer.toString(identifier)),true)
								.addField("VL", Integer.toString(level), false));
				//webhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription("Player hacker seems to be use cheat(module)".replace("cheat", hack)
						//.replace("module", module).replace("hacker", hacker.getName())));
				try {
					webhook.execute();
				} catch (IOException e) {

				}
			}
		}).start();
	}
}
