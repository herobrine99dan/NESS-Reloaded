package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public class TeleportEvent extends AbstractCheck<PlayerTeleportEvent> {

	public TeleportEvent(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerTeleportEvent.class, 1500, TimeUnit.MILLISECONDS));
	}

	@Override
	void checkEvent(PlayerTeleportEvent e) {
		if (e.getTo().getPitch() == Math.round(e.getTo().getPitch())) {
			if (e.getTo().getPitch() > 89) {
				e.getTo().setPitch(e.getTo().getPitch() - 0.01f);
			} else {
				e.getTo().setPitch(e.getTo().getPitch() + 0.01f);
			}
		} else if (e.getTo().getYaw() == Math.round(e.getTo().getYaw())) {
			if (e.getTo().getYaw() > 360) {
				e.getTo().setYaw(e.getTo().getYaw() - 0.01f);
			} else {
				e.getTo().setYaw(e.getTo().getYaw() + 0.01f);
			}
		}
		this.manager.getPlayer(e.getPlayer()).setTeleported(true);
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.setTeleported(false);
	}

}
