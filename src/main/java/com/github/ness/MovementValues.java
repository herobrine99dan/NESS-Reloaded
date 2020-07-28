package com.github.ness;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.ness.utility.Utility;

public class MovementValues {

	/**
	 * Yaw Difference (to.getYaw() - from.getYaw());
	 */
	public double yawDiff;
	/**
	 * Pitch Difference (to.getPitch() - from.getPitch());
	 */
	public double pitchDiff;
	/**
	 * X Difference (to.getX() - from.getX());
	 */
	public double xDiff;
	/**
	 * Y Difference (to.getY() - from.getY());
	 */
	public double yDiff;
	/**
	 * Z Difference (to.getZ() - from.getZ());
	 */
	public double zDiff;
	/**
	 * XZ Difference Math.abs(xDiff) + Math.abs(zDiff);
	 */
	public double XZDiff;
	/**
	 * Total Distance to.distance(from);
	 */
	public double totalDistance;
	/**
	 * XZ Multiplicated ((xDiff * xDiff) + (zDiff * zDiff));
	 */
	public double xzDiffMultiplier;
	public boolean AroundIce;
	/**
	 * Liquids= Lava and Water
	 */
	public boolean AroundLiquids;

	public boolean AroundStairs;
	/**
	 * Utility.specificBlockNear(to, "slime"); or Utility.hasBlock(p, "slime");
	 */
	public boolean AroundSlime;

	public MovementValues(Player p, Location to, Location from) {
		yawDiff = to.getYaw() - from.getYaw();
		pitchDiff = to.getPitch() - from.getPitch();
		xDiff = to.getX() - from.getX();
		yDiff = to.getY() - from.getY();
		zDiff = to.getZ() - from.getZ();
		XZDiff = Math.abs(xDiff) + Math.abs(zDiff);
		xzDiffMultiplier = (xDiff * xDiff) + (zDiff * zDiff);
		AroundIce = Utility.specificBlockNear(to, "ice");
		AroundLiquids = Utility.specificBlockNear(to, "liquid");
		AroundSlime = Utility.specificBlockNear(to, "slime");
		if (!AroundSlime) {
			AroundSlime = Utility.hasBlock(p, "slime");
		}
		totalDistance = to.distance(from);
	}

}
