package com.github.ness.check.movement.predictions;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class SpeedAirFriction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private int airTicks;
	private float lastDeltaXZ;

	public SpeedAirFriction(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	/**
	 * Powerful Air XZ-Prediction check made with https://www.mcpk.wiki/wiki/ Loving
	 * those guys who made it.
	 */
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		if (player.isFlying() || values.getHelper().isPlayerUsingElytra(nessPlayer)
				|| values.getHelper().isNearLiquid(event.getTo()) || values.getHelper().isNearLiquid(event.getFrom())) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		float xzDiff = (float) values.getXZDiff();
		final boolean sprinting = nessPlayer.getSprinting().get();
		// boolean onGround =
		// values.getHelper().isMathematicallyOnGround(values.getTo().getY());
		boolean lastOnGround = values.getHelper().isMathematicallyOnGround(values.getFrom().getY());
		if (!lastOnGround) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		float friction = 0.91f;
		float acceleration = sprinting ? 0.026f : 0.02f;
		;
		if (airTicks > 1) {
			float prediction = (lastDeltaXZ * friction) + acceleration; // Momentum + acceleration
			float result = xzDiff - prediction;
			this.player()
					.sendDevMessage("xzDiff: " + roundNumber(xzDiff) + " predict: " + roundNumber(prediction)
							+ " result: " + roundNumber(result) + " accel: " + roundNumber(acceleration)
							+ " lastXZDiff: " + roundNumber(lastDeltaXZ));
		}
		this.lastDeltaXZ = xzDiff;
	}

	private double roundNumber(double n) {
		return Math.round(n * 1000.0) / 1000.0;
	}
}
