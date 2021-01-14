package com.github.ness.data;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.ness.NessPlayer;
import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.utility.Utility;

//Need a refactor
public class MovementValues {

	/**
	 * Yaw Difference (to.getYaw() - from.getYaw());
	 */
	private final double yawDiff;
	/**
	 * Pitch Difference (to.getPitch() - from.getPitch());
	 */
	private final double pitchDiff;
	/**
	 * X Difference (to.getX() - from.getX());
	 */
	private final double xDiff;
	/**
	 * Y Difference (to.getY() - from.getY());
	 */
	private final double yDiff;
	/**
	 * Z Difference (to.getZ() - from.getZ());
	 */
	private final double zDiff;

	private final double XZDiff;

	private final boolean AroundIce;
	/**
	 * Liquids= Lava and Water
	 */

	private final boolean AroundLiquids;

	private final boolean AroundStairs;
	/**
	 * Utility.specificBlockNear(to, "slime"); or Utility.hasBlock(p, "slime");
	 */

	private final boolean AroundSlime;

	private final boolean AroundSlabs;

	private final boolean AroundChorus;

	private final boolean AroundSnow;

	private final boolean AroundLadders;

	private final boolean AroundLily;

	private final boolean AroundCarpet;

	private final boolean groundAround;

	private final boolean onGroundCollider;

	private final boolean aroundWeb;

	private final ImmutableVector serverVelocity;

	private final boolean insideVehicle;

	private final ImmutableLoc to;

	private final ImmutableLoc from;

	private final GameMode gamemode;

	private final boolean isFlying;

	private final boolean ableFly;

	private final boolean AroundFence;

	private final boolean AroundNonOccludingBlocks;

	private final boolean AroundGate;

	private final boolean AroundWalls;
	private final boolean AroundFire;
	private final boolean AroundStainedGlass;
	private final boolean AroundGlass;
	private final boolean sprinting;
	private final boolean blockUnderHead;
	private final boolean AroundSeaBlocks;
	private final MovementValuesHelper helper;
	private final Player player; // Not Thread Safe

	public MovementValues(NessPlayer nessPlayer, ImmutableLoc to, ImmutableLoc from, MaterialAccess access) {
		Player p = nessPlayer.getBukkitPlayer();
		this.to = to;
		this.from = from;
		this.player = p;
		this.helper = MovementValuesHelper.makeHelper(this);
		if (Bukkit.isPrimaryThread()) {
			boolean liquids = false;
			boolean ice = false;
			boolean slime = false;
			boolean stairs = false;
			boolean slab = false;
			boolean ladder = false;
			boolean fire = false;
			boolean snow = false;
			boolean lily = false;
			boolean carpet = false;
			boolean ground = false;
			boolean web = false;
			boolean chorus = false;
			boolean gate = false;
			boolean walls = false;
			if (this.helper.isMathematicallyOnGround(to.getY())
					&& this.helper.isOnGroundUsingCollider(to.toBukkitLocation(), access)) {
				nessPlayer.updateSafeLocation(to.toBukkitLocation());
			}
			boolean fence = false;
			boolean onGroundCollider = false;
			boolean sea = false;
			boolean stainedGlass = false;
			boolean glass = false;
			boolean nonOccludingBlocks = false;
			serverVelocity = new ImmutableVector(p.getVelocity().getX(), p.getVelocity().getY(),
					p.getVelocity().getZ());
			gamemode = p.getGameMode();
			isFlying = p.isFlying();
			ableFly = p.getAllowFlight();
			for (Block b : Utility.getBlocksAround(to.toBukkitLocation(), 2)) {
				Material material = access.getMaterial(b);
				String name = material.name();
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
				} else if (name.contains("SLAB") || name.contains("STEP")) {
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
				} else if (name.contains("WEB")) {
					web = true;
				} else if (name.contains("FENCE")) {
					fence = true;
				} else if (name.contains("GATE")) {
					gate = true;
				} else if (name.contains("WALL")) {
					walls = true;
				} else if (name.contains("FIRE")) {
					fire = true;
				} else if (name.contains("CHORUS")) {
					chorus = true;
				} else if (name.contains("SEA")) {
					sea = true;
				} else if (name.contains("STAINED")) {
					stainedGlass = true;
				} else if (name.contains("STAINED")) {
					glass = true;
				}
				if (material.isSolid() && !material.isOccluding()) {
					nonOccludingBlocks = true;
				}
			}
			AroundSnow = snow;
			AroundStainedGlass = stainedGlass;
			blockUnderHead = helper.groundAround(to.toBukkitLocation().add(0, 2.5, 0));
			AroundLadders = ladder;
			AroundSlabs = slab;
			AroundNonOccludingBlocks = nonOccludingBlocks;
			aroundWeb = web;
			AroundStairs = stairs;
			sprinting = p.isSprinting();
			if (!slime) {
				slime = helper.hasBlock(p, "SLIME");
			}
			AroundSlime = slime;
			AroundIce = ice;
			AroundLily = lily;
			groundAround = ground;
			insideVehicle = p.isInsideVehicle();
			AroundCarpet = carpet;
			AroundChorus = chorus;
			AroundLiquids = liquids;
			AroundFence = fence;
			AroundFire = fire;
			AroundSeaBlocks = sea;
			AroundGate = gate;
			AroundGlass = 
			AroundWalls = walls;
			onGroundCollider = this.isAroundCarpet() || this.isAroundSlabs() || this.isAroundStairs()
					|| this.isAroundSnow() || this.isAroundFence() || this.isAroundGate() || this.isAroundWalls();
			if (onGroundCollider) {
				this.onGroundCollider = onGroundCollider;
			} else {
				onGroundCollider = this.getHelper().isOnGroundUsingCollider(to.toBukkitLocation(), access);
				if (!onGroundCollider) {
					onGroundCollider = this.getHelper().isOnGroundUsingCollider(from.toBukkitLocation(), access);
				}
				this.onGroundCollider = onGroundCollider;
			}
		} else {
			AroundIce = false;
			AroundSlabs = false;
			serverVelocity = new ImmutableVector(0, 0, 0);
			AroundCarpet = false;
			AroundLadders = false;
			groundAround = false;
			AroundStainedGlass = false;
			AroundGate = false;
			AroundGlass = false;
			AroundWalls = false;
			sprinting = false;
			AroundNonOccludingBlocks = false;
			AroundLily = false;
			AroundFire = false;
			aroundWeb = false;
			blockUnderHead = false;
			onGroundCollider = false;
			AroundSnow = false;
			insideVehicle = false;
			gamemode = GameMode.SURVIVAL;
			isFlying = false;
			AroundChorus = false;
			ableFly = false;
			AroundLiquids = false;
			AroundSlime = false;
			AroundStairs = false;
			AroundFence = false;
			AroundSeaBlocks = false;
		}
		yawDiff = to.getYaw() - from.getYaw();
		pitchDiff = to.getPitch() - from.getPitch();
		xDiff = to.getX() - from.getX();
		yDiff = to.getY() - from.getY();
		zDiff = to.getZ() - from.getZ();
		XZDiff = Math.hypot(xDiff, zDiff);
	}

	public boolean hasBlockNearHead() {
		return blockUnderHead;
	}

	/**
	 * Check if the player is falling using his server Motion
	 * 
	 * @return true if the player is falling
	 */
	public boolean isServerFalling() {
		return this.serverVelocity.getY() < 0;
	}

	/**
	 * Check if the player is falling using his yDifference
	 * 
	 * @return true if the player is falling
	 */
	public boolean isClientFalling() {
		return this.yDiff < 0;
	}

	public ImmutableVector getDirection() {
		return this.getTo().getDirectionVector();
	}

	public ImmutableLoc getTo() {
		return to;
	}

	public ImmutableLoc getFrom() {
		return from;
	}

	public double getYawDiff() {
		return yawDiff;
	}

	public double getPitchDiff() {
		return pitchDiff;
	}

	public double getxDiff() {
		return xDiff;
	}

	public double getyDiff() {
		return yDiff;
	}

	public double getzDiff() {
		return zDiff;
	}

	public double getXZDiff() {
		return XZDiff;
	}

	public boolean isAroundIce() {
		return AroundIce;
	}

	public boolean isAroundLiquids() {
		return AroundLiquids;
	}

	public boolean isAroundStairs() {
		return AroundStairs;
	}

	public boolean isAroundSlime() {
		return AroundSlime;
	}

	public boolean isAroundSlabs() {
		return AroundSlabs;
	}

	public boolean isOnGroundCollider() {
		return onGroundCollider;
	}

	public boolean isAroundGate() {
		return AroundGate;
	}

	public boolean isAroundFence() {
		return AroundFence;
	}

	public boolean isAroundSnow() {
		return AroundSnow;
	}

	public boolean isAroundLadders() {
		return AroundLadders;
	}

	public boolean isAroundLily() {
		return AroundLily;
	}

	public boolean isAroundCarpet() {
		return AroundCarpet;
	}

	public boolean isGroundAround() {
		return groundAround;
	}

	public boolean isAroundWeb() {
		return aroundWeb;
	}

	public ImmutableVector getServerVelocity() {
		return serverVelocity;
	}

	public boolean isInsideVehicle() {
		return insideVehicle;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public boolean isAbleFly() {
		return ableFly;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public MovementValuesHelper getHelper() {
		return helper;
	}

	public boolean isAroundWalls() {
		return AroundWalls;
	}

	public boolean isAroundFire() {
		return AroundFire;
	}

	public boolean isAroundChorus() {
		return AroundChorus;
	}

	/**
	 * Executing actions on Player object in another thread isn't thread safe.
	 * Please, if you can, use values already calculated on MovementValues or on
	 * MovementValuesHelper
	 * 
	 * @return Player Object of this MovementValues object
	 */
	public Player getPlayer() {
		return player;
	}

	public boolean isAroundSeaBlocks() {
		return AroundSeaBlocks;
	}

	public boolean isAroundNonOccludingBlocks() {
		return AroundNonOccludingBlocks;
	}

	public boolean isAroundStainedGlass() {
		return AroundStainedGlass;
	}

	public boolean isAroundGlass() {
		return AroundGlass;
	}
}
