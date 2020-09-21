package com.github.ness.data;

import com.github.ness.utility.Utility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

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
	public final boolean AroundSlime;
	public final boolean AroundSlabs;
	public final boolean AroundSnow;
	public final boolean AroundLadders;
	public final boolean AroundLily;
	public final boolean AroundCarpet;
	public final boolean isOnGround;
	@Getter
	ImmutableLoc to;
	@Getter
	ImmutableLoc from;

	public MovementValues(Player p, ImmutableLoc to, ImmutableLoc from) {
		if (Bukkit.isPrimaryThread()) {
			/*AroundIce = Utility.specificBlockNear(to.toBukkitLocation(), "ice");
			// AroundLiquids = Utility.specificBlockNear(to.toBukkitLocation(), "liquid");
			boolean slimenear = Utility.specificBlockNear(to.toBukkitLocation(), "slime");
			if (!slimenear) {
				AroundSlime = Utility.hasBlock(p, "slime");
			} else {
				AroundSlime = Utility.specificBlockNear(to.toBukkitLocation(), "slime");
			}
			AroundStairs = Utility.specificBlockNear(to.toBukkitLocation(), "stair");
			AroundSlabs = Utility.specificBlockNear(to.toBukkitLocation(), "slab");
			boolean ladders = Utility.specificBlockNear(to.toBukkitLocation(), "ladder");
			if (!ladders) {
				ladders = Utility.specificBlockNear(to.toBukkitLocation(), "vine");
			}
			AroundLadders = ladders;
			AroundSnow = Utility.specificBlockNear(to.toBukkitLocation(), "snow");
			AroundLily = Utility.specificBlockNear(to.toBukkitLocation(), "lily");
			AroundCarpet = Utility.specificBlockNear(to.toBukkitLocation(), "carpet");*/
			//isOnGround = Utility.groundAround(p.getLocation().clone());
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
			for (Block b : Utility.getBlocksAround(to.toBukkitLocation(), 2)) {
				String name = b.getType().name();
				if (b.isLiquid()) {
					liquids = true;
				}
				if(b.getType().isSolid()) { //Doing all things in a single for loop is better than 12 loops
					ground = true;
				}
				if (name.contains("ice")) {
					ice = true;
				} else if (name.contains("slime")) {
					slime = true;
				}else if (name.contains("stair")) {
					stairs = true;
				}else if (name.contains("slab")) {
					slab = true;
				}else if (name.contains("ladder")) {
					ladder = true;
				}else if (name.contains("vine")) {
					ladder = true;
				} else if (name.contains("snow")) {
					snow = true;
				}else if (name.contains("lily")) {
					lily = true;
				}else if (name.contains("carpet")) {
					carpet = true;
				}
			}
			AroundSnow = snow;
			AroundLadders = ladder;
			AroundSlabs = slab;
			AroundStairs = stairs;
			if(!slime) {
				slime = Utility.hasBlock(p, "slime");
			}
			AroundSlime = slime;
			AroundIce = ice;
			AroundLily = lily;
			isOnGround = ground;
			AroundCarpet = carpet;
			AroundLiquids = liquids;
		} else {
			AroundIce = false;
			AroundSlabs = false;
			AroundCarpet = false;
			AroundLadders = false;
			isOnGround = false;
			AroundSnow = false;
			AroundLily = false;
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

	public ImmutableVector getDirection() {
		return this.getTo().getDirectionVector();
	}

	public CompletableFuture<Double> calculateAsyncWithCancellation(ImmutableLoc to, ImmutableLoc from)
			throws InterruptedException {
		CompletableFuture<Double> completableFuture = new CompletableFuture<Double>();
		Executors.newCachedThreadPool().submit(() -> {
			double value = to.getX() - from.getX();
			completableFuture.complete(value);
			return null;
		});

		return completableFuture;
	}

}
