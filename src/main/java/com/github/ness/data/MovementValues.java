package com.github.ness.data;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.ness.utility.Utility;

import lombok.Getter;

//Need a refactor, we should move some fields to NessPlayer
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
	public final boolean AroundIce;
	/**
	 * Liquids= Lava and Water
	 */
	public final boolean AroundLiquids;

	public final boolean AroundStairs;
	/**
	 * Utility.specificBlockNear(to, "slime"); or Utility.hasBlock(p, "slime");
	 */
	@Getter
	private final boolean AroundSlime;
	@Getter
	private final boolean AroundSlabs;
	@Getter
	private final boolean AroundSnow;
	@Getter
	private final boolean AroundLadders;
	@Getter
	private final boolean AroundLily;
	@Getter
	private final boolean AroundCarpet;
	@Getter
	private final boolean groundAround;
	@Getter
	private final ImmutableVector serverVelocity;
	@Getter
	private final boolean insideVehicle;
	@Getter
	ImmutableLoc to;
	@Getter
	ImmutableLoc from;
	@Getter
	private final GameMode gamemode;
	@Getter
	private final boolean isFlying;
	@Getter
	private final boolean ableFly;

	public MovementValues(Player p, ImmutableLoc to, ImmutableLoc from) {
		if (Bukkit.isPrimaryThread()) {
			boolean liquids = false;
			boolean ice = false;
			boolean slime = false;
			boolean stairs = false;
			boolean slab = false;
			boolean ladder = false;
			boolean snow = false;
			boolean lily = false;
			boolean carpet = false;
			boolean ground = false;
			serverVelocity = new ImmutableVector(p.getVelocity().getX(), p.getVelocity().getY(),
					p.getVelocity().getZ());
			gamemode = p.getGameMode();
			isFlying = p.isFlying();
			ableFly = p.getAllowFlight();
			for (Block b : Utility.getBlocksAround(to.toBukkitLocation(), 2)) {
				String name = b.getType().name();
				if (b.isLiquid()) {
					liquids = true;
				}
				if (b.getType().isSolid()) { // Doing all things in a single for loop is better than 12 loops
					ground = true;
				}
				if (name.contains("ICE")) {
					ice = true;
				} else if (name.contains("SLIME")) {
					slime = true;
				} else if (name.contains("STAIR")) {
					stairs = true;
				} else if (name.contains("SLAB")) {
					slab = true;
				} else if (name.contains("LADDER")) {
					ladder = true;
				} else if (name.contains("VINE")) {
					ladder = true;
				} else if (name.contains("SNOW")) {
					snow = true;
				} else if (name.contains("LILY")) {
					lily = true;
				} else if (name.contains("CARPET")) {
					carpet = true;
				}
			}
			AroundSnow = snow;
			AroundLadders = ladder;
			AroundSlabs = slab;
			AroundStairs = stairs;
			if (!slime) {
				slime = Utility.hasBlock(p, "SLIME");
			}
			AroundSlime = slime;
			AroundIce = ice;
			AroundLily = lily;
			groundAround = ground;
			insideVehicle = p.isInsideVehicle();
			AroundCarpet = carpet;
			AroundLiquids = liquids;
		} else {
			AroundIce = false;
			AroundSlabs = false;
			serverVelocity = new ImmutableVector(0, 0, 0);
			AroundCarpet = false;
			AroundLadders = false;
			groundAround = false;
			AroundLily = false;
			AroundSnow = false;
			insideVehicle = false;
			gamemode = GameMode.SURVIVAL;
			isFlying = false;
			ableFly = false;
			AroundLiquids = false;
			AroundSlime = false;
			AroundStairs = false;
		}
		yawDiff = to.getYaw() - from.getYaw();
		pitchDiff = to.getPitch() - from.getPitch();
		xDiff = to.getX() - from.getX();
		yDiff = to.getY() - from.getY();
		zDiff = to.getZ() - from.getZ();
		XZDiff = Math.abs(xDiff) + Math.abs(zDiff);
		this.to = to;
		this.from = from;
	}

	public void doCalculations() {

	}

	public ImmutableVector getDirection() {
		return this.getTo().getDirectionVector();
	}
}
