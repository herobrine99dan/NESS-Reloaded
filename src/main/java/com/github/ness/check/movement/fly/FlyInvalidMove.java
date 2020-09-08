package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

public class FlyInvalidMove extends AbstractCheck<PlayerMoveEvent> {

	public FlyInvalidMove(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getFallDistance() < 7 && player.getVelocity().getY() < -2.0D && !player.isFlying()) {
			this.getNessPlayer(player).setViolation(new Violation("Fly", "InvalidMove" + "FallDist: "
					+ (float) player.getFallDistance() + " Velocity: " + (float) player.getVelocity().getY()), event);
		}
	}

}
