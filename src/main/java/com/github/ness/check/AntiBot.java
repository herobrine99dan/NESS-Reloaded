package com.github.ness.check;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public class AntiBot extends AbstractCheck<AsyncPlayerPreLoginEvent> {
	int maxPlayers = 25;
	int playerCounter = 0;
	String message;
	public static ArrayList<String> whitelistbypass = new ArrayList<String>();

	public AntiBot(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(AsyncPlayerPreLoginEvent.class, 1, TimeUnit.SECONDS));
		message = "Bot Attack Detected! By NESS Reloaded";
		maxPlayers = 10;
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		this.playerCounter = 0;
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
