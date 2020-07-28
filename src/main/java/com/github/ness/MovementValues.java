package com.github.ness;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.ness.utility.Utility;

import lombok.Getter;

public class MovementValues {

	/**
	 * Test
	 */
	@Getter
	double yawDiff;
	@Getter
	double pitchDiff;
	@Getter
	double xDiff;
	@Getter
	double yDiff;
	@Getter
	double zDiff;
	@Getter
	/**
	 * This method calculate the xzDistance doing Math.abs(xDiff) + Math.abs(zDiff)
	 */
	double xZDiff;
	@Getter
	double totalDistance;
	@Getter
	double xzDiffMultiplier;
	@Getter
	boolean iceAround;
	@Getter
	boolean liquidsAround;
	@Getter
	boolean stairsAround;
	@Getter
	boolean slimeAround;
	
	public MovementValues(Player p, Location to, Location from) {
		yawDiff = to.getYaw() - from.getYaw();
		pitchDiff = to.getPitch() - from.getPitch();
		xDiff = to.getX() - from.getX();
		yDiff = to.getY() - from.getY();
		zDiff = to.getZ() - from.getZ();
		xZDiff = Math.abs(xDiff) + Math.abs(zDiff);
		xzDiffMultiplier = (xDiff * xDiff) + (zDiff * zDiff);
		iceAround = Utility.specificBlockNear(to, "ice");
		liquidsAround = Utility.specificBlockNear(to, "liquid");
		slimeAround = Utility.specificBlockNear(to, "slime");
		if(!slimeAround) {
			slimeAround = Utility.hasBlock(p, "slime");
		}
		totalDistance = to.distance(from);
	}
}
