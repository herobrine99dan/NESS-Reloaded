package com.github.ness.check;

import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.ness.CheckManager;

public class TeleportEvent extends AbstractCheck<PlayerTeleportEvent> {

	public TeleportEvent(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerTeleportEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerTeleportEvent e) {
		this.manager.getPlayer(e.getPlayer()).setTeleported(true);
	}

}
