package com.github.ness.check.misc;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ness.CheckManager;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

public class AntiBotWhitelist extends AbstractCheck<PlayerJoinEvent> {
	int maxSeconds = 5;

	public AntiBotWhitelist(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerJoinEvent.class));
	}

	@Override
	protected void checkEvent(PlayerJoinEvent e) {
		Check(e);
	}

	void Check(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (e.getPlayer().isOnline()) {
					if (!AntiBot.whitelistbypass.contains(e.getPlayer().getName())) {
						AntiBot.whitelistbypass.add(e.getPlayer().getName());
					}
				}
			}
		}.runTaskLater(manager.getNess(), maxSeconds * 20L);
	}
}
