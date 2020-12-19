package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableVector;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class SpeedFriction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	int airTicks;
	double lastDeltaXZ;
	int buffer;

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
		double xDiff = values.getxDiff();
		double zDiff = values.getzDiff();
		if (Utility.hasflybypass(player) || values.isAroundLiquids() || values.isAroundSlime()
				|| values.hasBlockNearHead()) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		double xzDiff = Math.hypot(xDiff, zDiff);

		if (!player.isOnGround()) {
			airTicks++;
		} else {
			airTicks = 0;
		}
		if (airTicks > 1) {
			ImmutableVector flying = this.handleSpeedInAirValue(values.getTo().getYaw(), player.isSprinting());
			final double prediction = (lastDeltaXZ * 0.91f) + (player.isSprinting() ? 0.026 : 0.02);
			final double difference = xzDiff - prediction;
			if (difference > 0.001) {
				if (++buffer > 3) {
					this.flagEvent(event);
				}
			} else if (buffer > 0) {
				buffer--;
			}
		}
		this.lastDeltaXZ = xzDiff;
	}

	public ImmutableVector handleSpeedInAirValue(float yaw, boolean isSprinting) {
		float jumpMovementFactor = 0.02f;
		if (isSprinting) {
			jumpMovementFactor += 0.02d * 0.3D;
		}
		return this.moveFlying(1, 1, jumpMovementFactor, yaw);
	}

	/*
	 * This Just Give 0.02 with walk and 0.026 sprinting
	 */
	public ImmutableVector moveFlying(float strafe, float forward, float friction, float yaw) {
		float f = strafe * strafe + forward * forward;
		if (f >= 1.0E-4F) {
			f = (float) Math.sqrt(f);
			if (f < 1.0F)
				f = 1.0F;
			f = friction / f;
			strafe *= f;
			forward *= f;
			float f1 = (float) Math.sin(yaw * 3.1415927F / 180.0F);
			float f2 = (float) Math.cos(yaw * 3.1415927F / 180.0F);
			double motionX = (strafe * f2 - forward * f1); // These values are added to the motionX/motionZ
			double motionZ = (forward * f2 + strafe * f1);
			return new ImmutableVector(motionX, 0, motionZ);
		}
		return new ImmutableVector(0, 0, 0);
	}

}
