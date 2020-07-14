package com.github.ness.check;

import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
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
		ConfigurationSection config = this.manager.getNess().getNessConfig().getCheck(this.getClass());
		maxPlayers = config.getInt("maxplayers", 10);
		String message = config.getString("message", "Bot Attack Detected! By NESS Reloaded");
		if (playerCounter > maxPlayers && !whitelistbypass.contains(e.getName())) {
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, message);
		} else {
			e.allow();
		}
	}

}
