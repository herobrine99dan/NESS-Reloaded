package com.github.ness.check.movement.predictions;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class Strafe extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public Strafe(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues movementValues = this.player().getMovementValues();
		float friction = getCorrectFriction();
		final double xzDiff = movementValues.getXZDiff();
		Vector from = new Vector(movementValues.getFrom().getX(), 0, movementValues.getFrom().getZ());
		Vector to = new Vector(movementValues.getTo().getX(), 0, movementValues.getTo().getZ());
		Vector moving = from.subtract(to).divide(new Vector(friction, 1, friction)); // With 0 we get an Exception doing
																						// 0÷0
		Vector direction = getDirectionOfOnlyYaw(movementValues.getTo().getYaw());
		float angle = (float) Math.toDegrees(moving.angle(direction));
		float subtraction = Math.abs(Math.round(Math.abs(angle)) - Math.abs(angle));
		if (this.player().milliSecondTimeDifference(PlayerAction.JOIN) > 2000) {
			if (subtraction < 0.001 && xzDiff > 0.15 && ++buffer > 1) {
				this.flagEvent(e, "Strafe: " + subtraction);
			} else if (angle < 0.001 && xzDiff > 0.15 && ++buffer > 1) {
				this.flagEvent(e, "IrregularStrafeAngle");
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
		}
	}

	private float getCorrectFriction() {
		return 0.91f * 0.6f; // Only for now
	}

	private Vector getDirectionOfOnlyYaw(double yaw) {
		Vector vector = new Vector();
		double rotX = Math.toRadians(yaw);
		vector.setY(0);// vector.setY(-Math.sin(Math.toRadians(rotY))); sin(0)=0
		double xz = 1.0;// double xz = Math.cos(Math.toRadians(rotY)); cos(0)=1
		vector.setX(-xz * Math.sin(rotX));
		vector.setZ(xz * Math.cos(rotX));

		return vector;
	}
}
