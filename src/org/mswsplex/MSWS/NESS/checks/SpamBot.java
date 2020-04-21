package org.mswsplex.MSWS.NESS.checks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.mswsplex.MSWS.NESS.PlayerManager;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class SpamBot {

	@SuppressWarnings("unchecked")
	public static void Check(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		PlayerManager.addAction("chatMessage", event.getPlayer());
		PlayerManager.setInfo("lastChat", (OfflinePlayer) player, System.currentTimeMillis());
		List<Double> chats = new ArrayList<Double>();
		if (PlayerManager.getInfo("chatTimes", (OfflinePlayer) player) != null) {
			chats = (List<Double>) PlayerManager.getInfo("chatTimes", (OfflinePlayer) player);
		}
		chats.add(Double.valueOf(System.currentTimeMillis()));
		PlayerManager.setInfo("chatTimes", (OfflinePlayer) player, chats);
		double lastClick = 0.0;
		double lastTime = 0.0;
		int times = 0;
		for (int i = 0; i < chats.size(); ++i) {
			final double d = chats.get(i);
			if (Math.abs(lastTime - (d - lastClick)) <= 5.0) {
				++times;
			}
			if (System.currentTimeMillis() - d > 5000.0) {
				chats.remove(i);
			}
			lastTime = d - lastClick;
			lastClick = d;
		}
		if (chats.size() > 20) {
			WarnHacks.warnHacks(player, "Spambot", 5 * (chats.size() - 8), -1.0, 62,"Spammer",false);
			PlayerManager.addLogMessage((OfflinePlayer) player, "Spammed: " + event.getMessage() + " TIME: %time%");
		}
		if (times > 4) {
			WarnHacks.warnHacks(player, "Spambot", 5 * (times - 2), -1.0, 63,"Spammer",false);
			PlayerManager.addLogMessage((OfflinePlayer) player, "Spammed: " + event.getMessage() + " TIME: %time%");
		}
	}

}
