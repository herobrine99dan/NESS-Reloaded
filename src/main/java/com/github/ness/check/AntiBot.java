package com.github.ness.check;

import java.util.ArrayList;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.github.ness.CheckManager;

public class AntiBot extends AbstractCheck<AsyncPlayerPreLoginEvent> {
	int maxPlayers = 10;
	public static int playerCounter = 0;
	public static ArrayList<String> whitelistbypass = new ArrayList<String>();
	
	public AntiBot(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(AsyncPlayerPreLoginEvent.class));
	}

	@Override
	void checkEvent(AsyncPlayerPreLoginEvent e) {
		Check(e);
	}

	public void Check(AsyncPlayerPreLoginEvent e) {
		playerCounter++;
		if(playerCounter>maxPlayers && !whitelistbypass.contains(e.getName())) {
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "AntiBot Attack Detected! By NESS Reloaded");
		} else {
			e.allow();
		}
	}

}
