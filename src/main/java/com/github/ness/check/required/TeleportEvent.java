package com.github.ness.check.required;

import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.ness.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

public class TeleportEvent extends AbstractCheck<PlayerTeleportEvent> {

	public TeleportEvent(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerTeleportEvent.class, 1500, TimeUnit.MILLISECONDS));
	}
	
	@Override
	protected void checkAsyncPeriodic(NESSPlayer player) {
		player.setTeleported(false);
	}
	
	protected void checkEvent(PlayerTeleportEvent e) {
		Location result = e.getTo().clone();
		if (e.getTo().getPitch() == Math.round(e.getTo().getPitch())) {
			if (e.getTo().getPitch() > 89) {
				result.setPitch(e.getTo().getPitch() - 0.01f);
			} else {
				result.setPitch(e.getTo().getPitch() + 0.01f);
			}
		} else if (e.getTo().getYaw() == Math.round(e.getTo().getYaw())) {
			if (e.getTo().getYaw() > 360) {
				result.setYaw(e.getTo().getYaw() - 0.01f);
			} else {
				result.setYaw(e.getTo().getYaw() + 0.01f);
			}
		}
		e.setTo(result);
		NESSPlayer nessPlayer = this.manager.getPlayer(e.getPlayer());
		if (!nessPlayer.hasSetback) {
			nessPlayer.setTeleported(true);
		}
		nessPlayer.hasSetback = false;
	}

}
