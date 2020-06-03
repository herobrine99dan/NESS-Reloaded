package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public class TeleportEvent extends AbstractCheck<PlayerTeleportEvent> {

	public TeleportEvent(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerTeleportEvent.class, 2, TimeUnit.SECONDS));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerTeleportEvent e) {
		this.manager.getPlayer(e.getPlayer()).setTeleported(true);
	}
	
	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.setTeleported(false);
	}

}
