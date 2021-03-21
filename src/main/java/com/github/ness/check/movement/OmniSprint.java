package com.github.ness.check.movement;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableLoc;
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
		//Minecraft is stupid: if you are in 1.13 you can sprint backwards in water
		MovementValues values = nessPlayer.getMovementValues();
		if(values.getHelper().isNearLiquid(event.getTo()) || values.getHelper().isNearLiquid(event.getFrom())) {
			return;
		}
		if (values.isSprinting()) {
			if (values.getServerVelocity().getY() > 0.0 || nessPlayer.getMovementValues().getYawDiff() > 10) {
				return;
			}
			Vector moving = values.getFrom().toBukkitLocation().clone()
					.subtract(values.getTo().toBukkitLocation().clone()).toVector();
			double angle = moving.angle(getDirection(values.getTo()));
			if (angle < 1.59 && values.getHelper().isMathematicallyOnGround(values.getTo().getY())) {
				if (++buffer > 3) {
					flagEvent(event, "Angle: " + (float) angle);
				}
			} else if (buffer > 0) {
				buffer -= 0.25;
			}
		}
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

}
