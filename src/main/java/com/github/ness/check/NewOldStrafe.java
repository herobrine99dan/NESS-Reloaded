package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class NewOldStrafe extends AbstractCheck<PlayerMoveEvent> {

	public NewOldStrafe(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		Vector dir = e.getTo().clone().subtract(e.getFrom()).toVector();

		double dist = distanceXZ(e.getFrom(), e.getTo());
		double yaw = e.getTo().getYaw();

		boolean isOnGround = p.isOnGround();

		yaw = yaw % 360;
		if (yaw < 0) {
			yaw += 360;
		}

		double angle = Math.toDegrees(Math.atan2(dir.getX(), dir.getZ()));
		double yawDiff = e.getTo().getYaw() - e.getFrom().getYaw();

		angle = -angle;
		if (angle < 0) {
			angle += 360;
		}

		if (np.lastStrafeAngle != 0 && Math.abs(np.lastStrafeAngle - angle) > 35
				&& Math.abs(np.lastStrafeAngle - angle) < 300 && Math.abs(yawDiff) < 8 && !p.isOnGround() && dist > .19
				&& !isAgainstBlock(e.getFrom()) && !isAgainstBlock(e.getTo())) {
			manager.getPlayer(p).setViolation(
					new Violation("NewOldStrafe", "High Angle Diff: " + Math.abs(np.lastStrafeAngle - angle)));
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
				if (loc.clone().add(x, 0.0001, z).getBlock().getType() != Material.AIR) {
					return true;
				}
			}
		}
		for (double x = -expand; x <= expand; x += expand) {
			for (double z = -expand; z <= expand; z += expand) {
				if (loc.clone().add(x, 1.0001, z).getBlock().getType() != Material.AIR) {
					return true;
				}
			}
		}
		return false;
	}
}
