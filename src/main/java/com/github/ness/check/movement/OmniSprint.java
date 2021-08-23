package com.github.ness.check.movement;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.LongRingBuffer;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class OmniSprint extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	
	private final double minAngle;

	public OmniSprint(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		minAngle = this.ness().getMainConfig().getCheckSection().omniSprint().minAngle();
	}
	
	public interface Config {
		@DefaultDouble(1.59)
		double minAngle();
	}

	private double buffer;
	private LongRingBuffer angles = new LongRingBuffer(10);

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		NessPlayer nessPlayer = player();
		// Minecraft is stupid: if you are in 1.13 you can sprint backwards in water
		MovementValues values = nessPlayer.getMovementValues();
		if (values.getHelper().isNearLiquid(event.getTo()) || values.getHelper().isNearLiquid(event.getFrom())
				|| values.isNearMaterials("ICE") || nessPlayer.getAcquaticUpdateFixes().isRiptiding()) {
			return;
		}
		if (nessPlayer.getSprinting().get() && values.getXZDiff() > 0.2) {
			Vector from = new Vector(values.getFrom().getX(), 0, values.getFrom().getZ());
			Vector to = new Vector(values.getTo().getX(), 0, values.getTo().getZ());
			Vector moving = from.subtract(to);
			double angleToStore = moving.angle(getDirectionOfOnlyYaw(values.getTo().getYaw()));
			angles.add((long) (angleToStore * 100000));
			if (angles.size() > 4) {
				float median = (float) angles.average() / 100000.0f;
				if (median < minAngle) {
					if (++buffer > 2) {
						flag("Angle: " + (float) median);
					}
				} else if (buffer > 0) {
					buffer -= 0.25;
				}
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
