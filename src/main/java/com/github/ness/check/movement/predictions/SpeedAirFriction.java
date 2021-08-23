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
	private float buffer;

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
		if (values.getHelper().hasflybypass(nessPlayer)
				|| values.getHelper().isNearLiquid(event.getTo()) || values.getHelper().isNearLiquid(event.getFrom())) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		float xzDiff = (float) values.getXZDiff();
		final boolean sprinting = nessPlayer.getSprinting().get() || player.isSprinting(); // Use also the old tick
																							// sprinting value
		// boolean onGround =
		// values.getHelper().isMathematicallyOnGround(values.getTo().getY());
		boolean onGround = values.getHelper().isMathematicallyOnGround(values.getTo().getY());
		if (!onGround) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		float friction = 0.91f;
		float acceleration = sprinting ? 0.026f : 0.02f;
		if (airTicks > 1) {
			float momentum = lastDeltaXZ * friction;
			float prediction = momentum + acceleration;
			float result = xzDiff - prediction;
			if (result > 1.0E-12D && prediction > 0.075D) {
				if (++this.buffer > 1) {
					this.flag("predictAccel: " + acceleration + " realAccell: " + roundNumber(xzDiff - momentum));
				}
			} else if (this.buffer > 0) {
				this.buffer -= 0.5f;
			}
		}
		this.lastDeltaXZ = xzDiff;
	}

	private double roundNumber(double n) {
		return Math.round(n * 1000.0) / 1000.0;
	}
}
