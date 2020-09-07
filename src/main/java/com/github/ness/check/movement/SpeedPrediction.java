package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class SpeedPrediction extends AbstractCheck<PlayerMoveEvent> {

	public SpeedPrediction(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent evt) {
		check(evt);
	}

	private void check(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		NessPlayer nessPlayer = this.manager.getPlayer(player);
		final double deltaXZ = Math.hypot(nessPlayer.getMovementValues().xDiff, nessPlayer.getMovementValues().zDiff);
		if (nessPlayer.airTicks >= 3 && !Utility.hasflybypass(player) && !Utility.isInWater(player)
				&& !Utility.specificBlockNear(e.getTo().clone(), "water")
				&& nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) > 1500 && !nessPlayer.isTeleported()) {
			double prediction = nessPlayer.lastDeltaXZSpeed;
			prediction *= 0.91F;
			double diff = deltaXZ - prediction;
			
			if (diff > 0.027 && nessPlayer.preVLSpeed++ > 0) {
				nessPlayer.setViolation(new Violation("Speed", "Invalid Friction " + (float) diff), e);
			} else {
				nessPlayer.preVLSpeed -= 1;
				if(nessPlayer.preVLSpeed < 0) {
					nessPlayer.preVLSpeed = 0;
				}
			}
		}
		nessPlayer.lastDeltaXZSpeed = deltaXZ;
	}
}