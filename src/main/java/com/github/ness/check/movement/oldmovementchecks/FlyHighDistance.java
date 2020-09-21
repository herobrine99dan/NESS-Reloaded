package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FlyHighDistance extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo.eventOnly(PlayerMoveEvent.class);

	public FlyHighDistance(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = player().getMovementValues();
		Player player = e.getPlayer();
		double dist = values.XZDiff;
		if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
				|| player().isTeleported()) {
			return;
		}
		if (player().nanoTimeDifference(PlayerAction.VELOCITY) < 1600) {
			dist -= Math.abs(player().velocity.getX()) + Math.abs(player().velocity.getZ());
		}
		if (!values.isOnGround && dist > 0.32 && values.yDiff == 0.0
				&& this.player().getTimeSinceLastWasOnIce() >= 1000) {
			this.player().setViolation(new Violation("Fly", "HighDistance(OnMove)"), e);
		}
	}

}
