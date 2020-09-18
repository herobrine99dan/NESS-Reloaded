package com.github.ness.check.misc;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AntiBot extends AbstractCheck<AsyncPlayerPreLoginEvent> {
	public static ArrayList<String> whitelistbypass = new ArrayList<String>();
	int maxPlayers;
	int playerCounter = 0;
	String message;

	public static final CheckInfo<AsyncPlayerPreLoginEvent> checkInfo = CheckInfo
			.eventOnly(AsyncPlayerPreLoginEvent.class);

	public AntiBot(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		message = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getString("message",
				"BotAttack Detected! By NESS Reloaded");
		maxPlayers = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getInt("maxplayers", 15);
	}

	@Override
	protected void checkAsyncPeriodic() {
		this.playerCounter = 0;
	}

	@Override
	protected void checkEvent(AsyncPlayerPreLoginEvent e) {
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
