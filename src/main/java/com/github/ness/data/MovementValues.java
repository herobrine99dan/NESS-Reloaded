package com.github.ness.data;

import java.util.HashSet;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.reflect.locator.VersionDetermination;
import com.github.ness.utility.Utility;

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

	private final boolean groundAround;

	private final Vector serverVelocity;

	private final boolean insideVehicle;

	private final ImmutableLoc to;

	private final ImmutableLoc from;

	private final GameMode gamemode;

	private final int hasBubblesColumns;
	private final boolean blockUnderHead;
	private static MovementValuesHelper helper;
	private static volatile HashSet<Material> trasparentMaterials = new HashSet<Material>();

	/**
	 * WARNING: USE THIS CONSTRUCTOR ONLY TO SETUP THE MOVEMENTVALUESHELPER OBJECT
	 * 
	 * @param MaterialAccess access
	 */
	public MovementValues(MaterialAccess access) {
		this.pitchDiff = 0;
		Objects.requireNonNull(access, "The MaterialAccess object provided is null!");
		helper = MovementValuesHelper.makeHelper(access);
		this.XZDiff = 0;
		this.xDiff = 0;
		this.to = null;
		this.from = null;
		this.yDiff = 0;
		this.yawDiff = 0;
		this.zDiff = 0;
		serverVelocity = new Vector(0, 0, 0);
		groundAround = false;
		blockUnderHead = false;
		insideVehicle = false;
		hasBubblesColumns = 0;
		gamemode = GameMode.SURVIVAL;
	}

	public MovementValues(NessPlayer nessPlayer, ImmutableLoc to, ImmutableLoc from, MaterialAccess access,
			VersionDetermination determination) {
		Player p = nessPlayer.getBukkitPlayer();
		this.to = to;
		this.from = from;
		if (Bukkit.isPrimaryThread()) {
			boolean ground = false;
			serverVelocity = new Vector(p.getVelocity().getX(), p.getVelocity().getY(), p.getVelocity().getZ());
			gamemode = p.getGameMode();
			for (Block b : Utility.getBlocksAround(to.toBukkitLocation(), 2)) {
				if (b.getType().isSolid()) { // Doing all things in a single loop is better than 12 loops
					ground = true;
				}
			}
			// blockUnderHead = helper.groundAround(to.toBukkitLocation().add(0, 2.5, 0));
			blockUnderHead = false;
			if (determination.hasAquaticUpdate()) { // We aren't stupid! columns of Bubbles don't exists in 1.12 or
													// below!
				hasBubblesColumns = 0;
			} else {
				hasBubblesColumns = isInColumnOfBubbles();
			}
			groundAround = ground;
			insideVehicle = p.isInsideVehicle();
			boolean colliderHorizon = this.getHelper().isCollidedHorizontally(to.toBukkitLocation());
			if (!colliderHorizon) {
				colliderHorizon = this.getHelper().isCollidedHorizontally(from.toBukkitLocation());
			}
			if (trasparentMaterials.size() == 0.0) { // We add in a set all the non-occluding materials
				trasparentMaterials = access.nonOccludingMaterials();
			}
		} else {
			serverVelocity = new Vector(0, 0, 0);
			groundAround = false;
			blockUnderHead = false;
			insideVehicle = false;
			hasBubblesColumns = 0;
			gamemode = GameMode.SURVIVAL;
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

	public Vector getDirection() {
		return this.getTo().getDirectionVector();
	}

	/**
	 * Check if the player is in a column of bubbles
	 * 
	 * @param magma
	 * @return 0 if there isn't a column, -1 if there is a column of magma bubbles
	 *         and 1 if there is a column of soulsand bubbles
	 */
	private int isInColumnOfBubbles() {
		int columnOfBubbles = 0;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				for (int y = (int) to.getY(); y > 1; y--) {
					Location loc = to.toBukkitLocation();
					Location cloned = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
					cloned.subtract(0, y, 0);
					if (cloned.getBlock().getType().name().contains("MAGMA")) {
						columnOfBubbles = -1;
						return columnOfBubbles;
					} else if (cloned.getBlock().getType().name().contains("SOUL")
							&& cloned.getBlock().getType().name().contains("SAND")) {
						columnOfBubbles = 1;
						return columnOfBubbles;
					}
				}
			}
		}
		return columnOfBubbles;
	}

	/**
	 * Is the player near Liquids? This method uses getHelper().isNearLiquid(loc)
	 * 
	 * @return
	 */
	public boolean isNearLiquid() {
		return getHelper().isNearLiquid(from.toBukkitLocation()) || getHelper().isNearLiquid(to.toBukkitLocation());
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

	public boolean isGroundAround() {
		return groundAround;
	}

	public Vector getServerVelocity() {
		return serverVelocity;
	}

	public boolean isInsideVehicle() {
		return insideVehicle;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	public MovementValuesHelper getHelper() {
		return helper;
	}

	/**
	 * This is a shortcut for the getHelper().isOnGround(loc) This checks both to
	 * and from locations
	 * 
	 * @return
	 */
	public boolean isOnGround() {
		return getHelper().isOnGround(to.toBukkitLocation()) || getHelper().isOnGround(from.toBukkitLocation());
	}

	/**
	 * This is a shortcut for the getHelper().isNearMaterial(material, loc) This
	 * checks both to and from locations
	 * 
	 * @return
	 */
	public boolean isNearMaterials(String... materials) {
		return getHelper().isNearMaterials(to.toBukkitLocation(), materials)
				|| getHelper().isNearMaterials(from.toBukkitLocation(), materials);
	}

	/**
	 * This is method checks if the player is near nonOccluding blocks This checks
	 * both to and from locations
	 * 
	 * @return
	 */
	public boolean isAroundNonOccludingBlocks() {
		for (Location newLoc : getHelper().getBoundingBoxesAroundPlayer(to.toBukkitLocation())) {
			if (!this.getHelper().getMaterialAccess().getMaterial(newLoc).isOccluding()
					&& this.getHelper().getMaterialAccess().getMaterial(newLoc).isSolid())
				return true;
		}
		for (Location newLoc : getHelper().getBoundingBoxesAroundPlayer(from.toBukkitLocation())) {
			if (!this.getHelper().getMaterialAccess().getMaterial(newLoc).isOccluding()
					&& this.getHelper().getMaterialAccess().getMaterial(newLoc).isSolid())
				return true;
		}
		return false;
	}

	public int hasBubblesColumns() {
		return hasBubblesColumns;
	}
}
