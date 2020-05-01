package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.PlayerManager;
import com.github.ness.CheckManager;
import com.github.ness.Violation;

public class SpamBot extends AbstractCheck<AsyncPlayerChatEvent> {

	public SpamBot(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(AsyncPlayerChatEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(AsyncPlayerChatEvent e) {
		Check(e);
	}

	@SuppressWarnings("unchecked")
	public void Check(AsyncPlayerChatEvent event) {
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
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("SpamBot"));
			PlayerManager.addLogMessage((OfflinePlayer) player, "Spammed: " + event.getMessage() + " TIME: %time%");
		}
		if (times > 4) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("SpamBot"));
			PlayerManager.addLogMessage((OfflinePlayer) player, "Spammed: " + event.getMessage() + " TIME: %time%");
		}
	}

}
