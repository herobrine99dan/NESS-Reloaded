package com.github.ness.check.movement;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.MathUtils;

public class SpeedFriction extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	int airTicks;
	double lastDeltaXZ;
	private int buffer;
	private float moveStrafe = 0, moveForward = 0;
	private float motionX, motionZ, lastMotionX, lastMotionZ;

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
		if (player.isFlying() || values.isAroundLiquids() || values.isAroundSlime() || values.hasBlockNearHead()) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			return;
		}
		double xzDiff = values.getXZDiff();
		if (!values.getHelper().isMathematicallyOnGround(values.getTo().getY())) {
			airTicks++;
		} else {
			airTicks = 0;
		}

		if (airTicks > 1) {
			final double prediction = (lastDeltaXZ * 0.91f) + 0.026;
			final double difference = xzDiff - prediction;
			if (difference > 1E-12 && prediction > 0.075) {
				if (++buffer > 1) {
					this.flagEvent(event);
				}
			} else if (buffer > 0) {
				buffer -= 0.5;
			}

		}
		this.lastDeltaXZ = xzDiff;
	}

	private void updateStrafeAndForward() {
		NessPlayer nessPlayer = this.player();
		MovementValues values = nessPlayer.getMovementValues();
		float motionYaw = (float) (Math.atan2(nessPlayer.getMovementValues().getzDiff(),
				nessPlayer.getMovementValues().getxDiff()) * 180.0D / Math.PI) - 90.0F;
		Vector moving = values.getFrom().toBukkitLocation().clone().subtract(values.getTo().toBukkitLocation().clone())
				.toVector();
		double angle = moving.angle(getDirection(values.getTo()));
		if (angle > 1.65) {
			moveForward = 1;
		} else if (angle < 1.0) {
			moveForward = -1;
		}
		int direction = 6;
		motionYaw -= nessPlayer.getMovementValues().getTo().getYaw();
		while (motionYaw > 360.0F)
			motionYaw -= 360.0F;
		while (motionYaw < 0.0F)
			motionYaw += 360.0F;
		motionYaw /= 45.0F;
		direction = (int) (new BigDecimal(motionYaw)).setScale(1, RoundingMode.HALF_UP).doubleValue();
		if (direction == 2.0) {
			moveStrafe = -1.0f;
		} else if (direction == 6.0) {
			moveStrafe = 1.0f;
		} else if (direction == 7.0) {
			moveStrafe = -1.0f;
		} else if (direction == 1.0) {
			moveStrafe = 1.0f;
		} else if (direction == 5.0) {
			moveStrafe = 1.0f;
		} else if (direction == 3.0) {
			moveStrafe = -1.0f;
		}
		nessPlayer.sendDevMessage("moveForward: " + moveForward + " moveStrafe: " + moveStrafe);
	}

	private Vector getDirection(ImmutableLoc loc) {
		Vector vector = new Vector();
		double rotX = loc.getYaw();
		double rotY = 3;
		vector.setY(-Math.sin(Math.toRadians(rotY)));
		double xz = Math.cos(Math.toRadians(rotY));
		vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
		vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
		return vector;
	}
	
	public float jumpMovementFactor = 0.02F;
	
	private void process() {
		motionX = lastMotionX;
		motionZ = lastMotionZ;
        this.jumpMovementFactor = 0.02F;

        if (this.player().getBukkitPlayer().isSprinting())
        {
            this.jumpMovementFactor = (float)(this.jumpMovementFactor + 0.02F * 0.3D);
        }
		moveFlying();
		motionX *= 0.91f;
		motionZ *= 0.91f;
	}

	public void moveFlying() {
		float f = moveStrafe * moveStrafe + moveForward * moveForward;
		if (f >= 1.0E-4F) {
			f = (float) Math.sqrt(f);
			if (f < 1.0F)
				f = 1.0F;
			f = jumpMovementFactor / f;
			moveStrafe *= f;
			moveForward *= f;
			float rotationYaw = this.player().getMovementValues().getTo().getYaw();
			float f1 = (float) MathUtils.sin(rotationYaw * 3.1415927F / 180.0F);
			float f2 = (float) MathUtils.cos(rotationYaw * 3.1415927F / 180.0F);
			this.motionX += (moveStrafe * f2 - moveForward * f1);
			this.motionZ += (moveForward * f2 + moveStrafe * f1);
		}
	}

}
