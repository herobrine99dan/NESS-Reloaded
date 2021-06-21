package com.github.ness.check.movement;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.movement.predictionhelper.PlayerPrediction;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.LongRingBuffer;

public class PredictionMovement extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public PredictionMovement(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
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
		// this.player().sendDevMessage("Angle: " + angle);
		float subtraction = Math.abs(Math.round(Math.abs(angle)) - Math.abs(angle));
		if (subtraction < 0.001 && xzDiff > 0.1 && ++buffer > 2) {
			this.flag("Strafe: " + subtraction);
		} else if (angle < 0.001 && xzDiff > 0.1 && ++buffer > 2) {
			this.flag("IrregularStrafeAngle");
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		float[] values = getForwardAndStrafe(angle);
		//makeThePrediction(values[0], values[1]);
	}

	private LongRingBuffer angles = new LongRingBuffer(5);

	private float[] getForwardAndStrafe(float angle) {
		angles.add((long) (angle * 100000));
		double median = angles.median() / 100000.0;
		int forwardMedian = 0;
		int strafeMedian = 0;
		// this.player().sendDevMessage("median: " + median);
		if (median > 140) {
			forwardMedian = 1;
		} else if (median < 50) {
			forwardMedian = -1;
		}
		double subtractionForStrafe = Math.abs(90 - median);
		if (subtractionForStrafe < 40) {
			strafeMedian = 1;
		}
		this.player().sendDevMessage("forward: " + forwardMedian + " strafe: " + strafeMedian);
		return new float[] { forwardMedian, strafeMedian };
	}

	/*private void makeThePrediction(float moveForward, float moveStrafe) {
		PlayerPrediction player = this.player().getPredictionMotion();
		MovementValues movementValues = this.player().getMovementValues();
		player.setAngles(movementValues.getTo().getYaw(), 0);
		float[] toReturn = player.tick(moveForward, moveStrafe);
		// this.player().sendDevMessage("xzDiff: " + xzDiff);
		// this.player().sendDevMessage("xzDiff: " + (float)
		// movementValues.getXZDiff());
		this.player().sendDevMessage(
				"xReal: " + (float) movementValues.getxDiff() + " zReal: " + (float) movementValues.getzDiff());
		this.player().sendDevMessage("xCalculated: " + toReturn[0] + " zCalculated: " + toReturn[2]);
	}*/

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
