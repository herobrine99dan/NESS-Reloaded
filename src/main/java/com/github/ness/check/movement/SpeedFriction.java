package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class SpeedFriction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	int airTicks;
	double lastDeltaXZ;

	public SpeedFriction(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	/**
	 * Simple Friction Check Made with help of Frap
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		double xDiff = values.xDiff;
		double zDiff = values.zDiff;
		if (Utility.hasflybypass(player) || values.isAroundLiquids()
				|| values.isAroundSlime() || values.hasBlockNearHead()) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			xDiff -= Math.abs(nessPlayer.getLastVelocity().getX());
			zDiff -= Math.abs(nessPlayer.getLastVelocity().getZ());
		}
		double xzDiff = Math.hypot(xDiff, zDiff);

		if (!player.isOnGround()) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		if (airTicks > 1) { //TODO Add a buffer that starts from 2
			final double prediction = lastDeltaXZ * 0.91f + (player.isSprinting() ? 0.026 : 0.02);
			final double difference = xzDiff - prediction;
			if (difference > 1e-5) {
				nessPlayer.sendDevMessage("CHEATS!");
			}
		}
		this.lastDeltaXZ = xzDiff;
	}

}
