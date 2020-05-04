package com.github.ness.check;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ness.CheckManager;

public class SpamBot extends AbstractCheck<AsyncPlayerChatEvent> {

	public SpamBot(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(AsyncPlayerChatEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(AsyncPlayerChatEvent e) {
		Check(e);
	}

	public void Check(AsyncPlayerChatEvent event) {

	}

}
