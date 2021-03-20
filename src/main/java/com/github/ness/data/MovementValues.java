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

	private final boolean aroundIce;
	/**
	 * Liquids= Lava and Water
	 */

	private final boolean aroundLiquids;

	private final boolean aroundStairs;
	/**
	 * Utility.specificBlockNear(to, "slime"); or Utility.hasBlock(p, "slime");
	 */

	private final boolean aroundSlime;

	private final boolean aroundSlabs;

	private final boolean aroundChorus;

	private final boolean aroundSnow;

	private final boolean aroundLadders;

	private final boolean aroundLily;

	private final boolean aroundIronBars;

	private final boolean aroundCarpet;

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

	private final boolean aroundCactus;

	private final boolean aroundFence;

	private final boolean aroundNonOccludingBlocks;

	private final boolean aroundGate;
	
	private final boolean aroundKelp;

	private final double dTG;

	private final boolean aroundWalls;
	private final boolean aroundFire;
	private final boolean aroundStainedGlass;
	private final boolean aroundGlass;
	private final boolean sprinting;
	private final boolean blockUnderHead;
	private final boolean aroundSeaBlocks;
	private static final MovementValuesHelper helper = MovementValuesHelper.makeHelper();
	private final Player player; // Not Thread Safe

	public MovementValues(NessPlayer nessPlayer, ImmutableLoc to, ImmutableLoc from, MaterialAccess access) {
		Player p = nessPlayer.getBukkitPlayer();
		this.to = to;
		this.from = from;
		this.player = p;
		if (Bukkit.isPrimaryThread()) {
			boolean liquids = false;
			boolean ironbars = false;
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
			boolean cactus = false;
			boolean kelp = false;
			boolean glass = false;
			boolean walls = false;
			if (this.helper.isMathematicallyOnGround(to.getY())
					&& this.helper.isOnGroundUsingCollider(to.toBukkitLocation(), access)) {
				nessPlayer.updateSafeLocation(to.toBukkitLocation());
			}
			boolean fence = false;
			boolean onGroundCollider = false;
			boolean sea = false;
			boolean stainedGlass = false;
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
				if (b.getType().isSolid()) { // Doing all things in a single loop is better than 12 loops
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
				} else if (name.contains("PAD")) {
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
				} else if (name.contains("BARS")) {
					ironbars = true;
				} else if (name.contains("CACTUS")) {
					cactus = true;
				} else if (name.contains("GLASS")) {
					glass = true;
				} else if (name.contains("KELP")) {
					kelp = true;
				}
				if (material.isSolid() && !material.isOccluding()) {
					nonOccludingBlocks = true;
				}
			}
			aroundSnow = snow;
			aroundStainedGlass = stainedGlass;
			blockUnderHead = helper.groundAround(to.toBukkitLocation().add(0, 2.5, 0));
			aroundLadders = ladder;
			aroundSlabs = slab;
			aroundNonOccludingBlocks = nonOccludingBlocks;
			aroundWeb = web;
			aroundStairs = stairs;
			aroundIronBars = ironbars;
			sprinting = p.isSprinting();
			if (!slime) {
				slime = helper.hasBlock(p, "SLIME");
			}
			aroundSlime = slime;
			aroundIce = ice;
			aroundLily = lily;
			aroundCactus = cactus;
			groundAround = ground;
			insideVehicle = p.isInsideVehicle();
			aroundCarpet = carpet;
			aroundKelp = kelp;
			aroundChorus = chorus;
			aroundLiquids = liquids;
			aroundFence = fence;
			aroundFire = fire;
			aroundSeaBlocks = sea;
			aroundGate = gate;
			aroundGlass = glass;
			aroundWalls = walls;
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
			dTG = makeDTG();
		} else {
			aroundIce = false;
			aroundSlabs = false;
			serverVelocity = new ImmutableVector(0, 0, 0);
			aroundCarpet = false;
			aroundLadders = false;
			groundAround = false;
			aroundStainedGlass = false;
			aroundGate = false;
			aroundGlass = false;
			aroundWalls = false;
			sprinting = false;
			aroundKelp = false;
			aroundNonOccludingBlocks = false;
			aroundLily = false;
			aroundIronBars = false;
			aroundFire = false;
			aroundWeb = false;
			blockUnderHead = false;
			onGroundCollider = false;
			aroundSnow = false;
			aroundCactus = false;
			insideVehicle = false;
			gamemode = GameMode.SURVIVAL;
			isFlying = false;
			aroundChorus = false;
			ableFly = false;
			dTG = 0.0;
			aroundLiquids = false;
			aroundSlime = false;
			aroundStairs = false;
			aroundFence = false;
			aroundSeaBlocks = false;
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

	private double makeDTG() {
		double dTG = 0.0;
	    for (int x = -1; x <= 1; x++) {
	        for (int z = -1; z <= 1; z++) {
	          int y = 0;
	          while (!player.getLocation().subtract(x, y, z).getBlock().getType().isSolid() && y < 20)
	            y++; 
	          if (y < dTG || dTG == 0.0D)
	            dTG = y; 
	        } 
	      } 
	      dTG += player.getLocation().getY() % 1.0D;
		return dTG;
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
		return aroundIce;
	}

	public boolean isAroundLiquids() {
		return aroundLiquids;
	}

	public boolean isAroundStairs() {
		return aroundStairs;
	}

	public boolean isAroundSlime() {
		return aroundSlime;
	}

	public boolean isAroundSlabs() {
		return aroundSlabs;
	}

	public boolean isOnGroundCollider() {
		return onGroundCollider;
	}

	public boolean isAroundGate() {
		return aroundGate;
	}

	public boolean isAroundFence() {
		return aroundFence;
	}

	public boolean isAroundSnow() {
		return aroundSnow;
	}

	public boolean isAroundLadders() {
		return aroundLadders;
	}

	public boolean isAroundLily() {
		return aroundLily;
	}

	public boolean isAroundCarpet() {
		return aroundCarpet;
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
		return aroundWalls;
	}

	public boolean isAroundFire() {
		return aroundFire;
	}

	public boolean isAroundChorus() {
		return aroundChorus;
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
		return aroundSeaBlocks;
	}

	public boolean isAroundNonOccludingBlocks() {
		return aroundNonOccludingBlocks;
	}

	public boolean isAroundStainedGlass() {
		return aroundStainedGlass;
	}

	public boolean isAroundGlass() {
		return aroundGlass;
	}

	public boolean isAroundIronBars() {
		return aroundIronBars;
	}

	public boolean isAroundCactus() {
		return aroundCactus;
	}

	public double getdTG() {
		return dTG;
	}

	public boolean isAroundKelp() {
		return aroundKelp;
	}
}
