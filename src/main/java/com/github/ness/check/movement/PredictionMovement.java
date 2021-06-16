package com.github.ness.check.movement;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class PredictionMovement extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public PredictionMovement(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = this.player().getMovementValues();
		float friction = getCorrectFriction();
		Vector from = new Vector(values.getFrom().getX(), 0, values.getFrom().getZ());
		final double xzDiff = values.getXZDiff();
		Vector to = new Vector(values.getTo().getX(), 0, values.getTo().getZ());
		Vector moving = from.subtract(to).divide(new Vector(friction, 1, friction)); // With 0 we get an Exception doing
																						// 0÷0
		Vector direction = getDirectionOfOnlyYaw(values.getTo().getYaw());
		float angle = (float) Math.toDegrees(moving.angle(direction));
		//Vector angle1 = moving.clone().crossProduct(direction);
		//this.player().sendDevMessage("Angle: " + angle);
		//this.player().sendDevMessage("Vector: " + (float) Math.toDegrees(angle1.getY()));
		float subtraction = Math.abs(Math.round(Math.abs(angle)) - Math.abs(angle));
		if (subtraction < 0.001 && xzDiff > 0.1 && ++buffer > 2) {
			this.flag("Strafe: " + subtraction);
		} else if (angle < 0.001 && xzDiff > 0.1 && ++buffer > 2) {
			this.flag("IrregularStrafeAngle");
		} else if(buffer>0) {
			buffer -= 0.5;
		}
	}

	private float getCorrectFriction() {
		return 0.91f; // Only for now
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
