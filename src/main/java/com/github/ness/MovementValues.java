package com.github.ness;

import org.bukkit.entity.Player;

import com.github.ness.utility.Utility;

public class MovementValues {

	/**
	 * Yaw Difference (to.getYaw() - from.getYaw());
	 */
	public final double yawDiff;
	/**
	 * Pitch Difference (to.getPitch() - from.getPitch());
	 */
	public final double pitchDiff;
	/**
	 * X Difference (to.getX() - from.getX());
	 */
	public final double xDiff;
	/**
	 * Y Difference (to.getY() - from.getY());
	 */
	public final double yDiff;
	/**
	 * Z Difference (to.getZ() - from.getZ());
	 */
	public final double zDiff;
	/**
	 * XZ Difference Math.abs(xDiff) + Math.abs(zDiff);
	 */
	public final double XZDiff;
	/**
	 * XZ Multiplicated ((xDiff * xDiff) + (zDiff * zDiff));
	 */
	public final double xzDiffMultiplier;
	public final boolean AroundIce;
	/**
	 * Liquids= Lava and Water
	 */
	public final boolean AroundLiquids;

	public final boolean AroundStairs;
	/**
	 * Utility.specificBlockNear(to, "slime"); or Utility.hasBlock(p, "slime");
	 */
	public final boolean AroundSlime;

	public MovementValues(Player p, ImmutableLoc to, ImmutableLoc from) {
		AroundIce = Utility.specificBlockNear(Utility.locationFromImmutableLoc(to), "ice");
		AroundLiquids = Utility.specificBlockNear(Utility.locationFromImmutableLoc(to), "liquid");
		boolean slimenear = Utility.specificBlockNear(Utility.locationFromImmutableLoc(to), "slime");
		if (!slimenear) {
			AroundSlime = Utility.hasBlock(p, "slime");
		} else {
			AroundSlime = Utility.specificBlockNear(Utility.locationFromImmutableLoc(to), "slime");
		}
		AroundStairs = Utility.specificBlockNear(Utility.locationFromImmutableLoc(to), "stair");
		
		yawDiff = to.getYaw() - from.getYaw();
		pitchDiff = to.getPitch() - from.getPitch();
		xDiff = to.getX() - from.getX();
		yDiff = to.getY() - from.getY();
		zDiff = to.getZ() - from.getZ();
		XZDiff = Math.abs(xDiff) + Math.abs(zDiff);
		xzDiffMultiplier = (xDiff * xDiff) + (zDiff * zDiff);
	}

}
