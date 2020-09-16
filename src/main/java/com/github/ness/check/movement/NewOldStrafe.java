package com.github.ness.check.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class NewOldStrafe extends AbstractCheck<PlayerMoveEvent> {

	public NewOldStrafe(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NESSPlayer np = this.manager.getPlayer(p);
		Vector dir = e.getTo().clone().subtract(e.getFrom()).toVector();

		double dist = distanceXZ(e.getFrom(), e.getTo());
		double angle = Math.toDegrees(Math.atan2(dir.getX(), dir.getZ()));
		double yawDiff = np.getMovementValues().yawDiff;

		angle = -angle;
		if (angle < 0) {
			angle += 360;
		}
		double result = Math.abs(np.lastStrafeAngle - angle);
		if (np.lastStrafeAngle != 0 && result > 35 && result < 300 && Math.abs(yawDiff) < 8 && !p.isOnGround()
				&& dist > .19 && !isAgainstBlock(e.getFrom()) && !isAgainstBlock(e.getTo())) {
			manager.getPlayer(p)
					.setViolation(new Violation("Strafe", "High Angle Diff: " + Math.abs(np.lastStrafeAngle - angle)), e);
		}

		np.lastStrafeAngle = angle;
	}

	double distanceXZ(Location loc1, Location loc2) {
		return Math.sqrt(
				Math.pow(Math.abs(loc1.getX() - loc2.getX()), 2) + Math.pow(Math.abs(loc1.getZ() - loc2.getZ()), 2));
	}

	boolean isAgainstBlock(Location loc) {
		double expand = 0.31;
		for (double x = -expand; x <= expand; x += expand) {
			for (double z = -expand; z <= expand; z += expand) {
				if (!Utility.getMaterialName(loc.clone().add(x, 0.0001, z)).contains("air")) {
					return true;
				}
			}
		}
		for (double x = -expand; x <= expand; x += expand) {
			for (double z = -expand; z <= expand; z += expand) {
				if (!Utility.getMaterialName(loc.clone().add(x, 1.0001, z)).contains("air")) {
					return true;
				}
			}
		}
		return false;
	}
}
