package com.github.ness.check;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.ness.CheckManager;

public class AntiBotWhitelist extends AbstractCheck<PlayerJoinEvent> {
	int maxSeconds = 5;
	public AntiBotWhitelist(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerJoinEvent.class));
	}

	@Override
	void checkEvent(PlayerJoinEvent e) {
		Check(e);
	}

	void Check(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(e.getPlayer().isOnline()) {
					if(!AntiBot.whitelistbypass.contains(e.getPlayer().getName())) {
						AntiBot.whitelistbypass.add(e.getPlayer().getName());
					}
				}
			}
		}.runTaskLater(manager.getNess(), maxSeconds*10L);
	}
}
