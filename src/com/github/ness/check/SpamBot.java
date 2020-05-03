package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

	}

}
