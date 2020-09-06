package com.github.ness.check.movement;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpeedPrediction extends AbstractCheck<PlayerMoveEvent> {

	public SpeedPrediction(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent evt) {
		check(evt);
	}

	private void check(PlayerMoveEvent e) {
		NessPlayer nessPlayer = this.manager.getPlayer(e.getPlayer());
		final double deltaXZ = nessPlayer.getMovementValues().XZDiff;

		if (nessPlayer.airTicks >= 3 && !Utility.hasflybypass(e.getPlayer()) && !Utility.isInWater(e.getPlayer())
				&& !Utility.specificBlockNear(e.getTo().clone(), "water")
				&& nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) > 1500 && !nessPlayer.isTeleported()) {
			double prediction = nessPlayer.lastDeltaXZ * 0.91F;
			double diff = deltaXZ - prediction;
			if (diff > 0.027) {
				if (++nessPlayer.speedThreshold > 1) {
					manager.getPlayer(e.getPlayer()).setViolation(new Violation("Speed", "Invalid Friction."), e);
				}
			} else
				nessPlayer.speedThreshold = 0;
		}
		nessPlayer.lastDeltaXZ = deltaXZ;
	}
}