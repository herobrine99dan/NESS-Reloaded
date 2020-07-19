package com.github.ness.check;

import java.util.ArrayList;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.github.ness.CheckManager;

public class AntiBot extends AbstractCheck<AsyncPlayerPreLoginEvent> {
	int maxPlayers = 10;
	public static int playerCounter = 0;
	String message;
	public static ArrayList<String> whitelistbypass = new ArrayList<String>();

	public AntiBot(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(AsyncPlayerPreLoginEvent.class));
		message = "Bot Attack Detected! By NESS Reloaded";
		maxPlayers = 10;
	}

	@Override
	void checkEvent(AsyncPlayerPreLoginEvent e) {
		Check(e);
	}

	public void Check(AsyncPlayerPreLoginEvent e) {
		playerCounter++;
		if (playerCounter > maxPlayers && !whitelistbypass.contains(e.getName())) {
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, message);
		} else {
			e.allow();
		}
	}

}
