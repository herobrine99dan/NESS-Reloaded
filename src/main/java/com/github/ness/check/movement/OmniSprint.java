package com.github.ness.check.movement;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;

public class OmniSprint extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public OmniSprint(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		NessPlayer nessPlayer = player();
		// Minecraft is stupid: if you are in 1.13 you can sprint backwards in water
		MovementValues values = nessPlayer.getMovementValues();
		if (values.getHelper().isNearLiquid(event.getTo()) || values.getHelper().isNearLiquid(event.getFrom())
				|| values.isAroundIce() || nessPlayer.getAcquaticUpdateFixes().isRiptiding()) {
			return;
		}
		if (values.isSprinting() && values.getXZDiff() > 0.25) {
			if (values.getServerVelocity().getY() > 0.0) {
				return;
			}
			Vector from = new Vector(values.getFrom().getX(), 0, values.getFrom().getZ());
			Vector to = new Vector(values.getTo().getX(), 0, values.getTo().getZ());
			Vector moving = from.subtract(to);
			double angle = moving.angle(getDirectionOfOnlyYaw(values.getTo().getYaw()));
			if (angle < 1.59) {
				if (++buffer > 2) {
					flagEvent(event, "Angle: " + (float) angle);
				}
			} else if (buffer > 0) {
				buffer -= 0.25;
			}
		}
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
