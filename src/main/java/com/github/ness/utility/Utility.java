package com.github.ness.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.ness.blockgetter.MaterialAccess;

public class Utility {

	public final static Set<Material> occludingMaterials = new HashSet<Material>();

	static {
		for (Material m : Material.values()) {
			if (m.isBlock() && m.isOccluding()) {
				occludingMaterials.add(m);
			}
		}
	}

	/**
	 * Round a value
	 *
	 * @param value
	 * @param precision (For example 100 will round 0.25065 to 0.25)
	 * @return
	 */
	public static double round(double value, int precision) {
		return (double) Math.round(value * precision) / precision;
	}

	public static Location getEyeLocation(LivingEntity player) {
		final Location eye = player.getLocation();
		eye.setY(eye.getY() + player.getEyeHeight());
		return eye;
	}

	/**
	 * Get Angle (in Radians) beetween a player and the target
	 * 
	 * @param player
	 * @param target
	 * @param direction (if it is null, will be replaced with player direction)
	 * @return the angle in radians
	 */
	public static double getAngle(Player player, Location target, Vector direction) {
		Location eye = player.getEyeLocation();
		if (direction == null) {
			direction = player.getLocation().getDirection();
		}
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(direction);
		return dot;
	}

	@SuppressWarnings("deprecation")
	public static boolean isItemInHand(Player p, String m) {
		if (Bukkit.getVersion().contains("1.8")) {
			return p.getItemInHand().getType().name().contains(m); // 1.8 Version doesn't have the second hand!
		} else {
			PlayerInventory inventory = p.getInventory();
			return inventory.getItemInMainHand().getType().name().contains(m)
					|| inventory.getItemInOffHand().getType().name().contains(m);
		}
	}

	/**
	 * Get the material name from the location and convert it to lowercase
	 *
	 * @param loc
	 * @return the material name
	 */
	public static String getMaterialName(Location loc) {
		return loc.getBlock().getType().name();
	}

	/**
	 * Check if a block is climbable
	 *
	 * @param b
	 * @return
	 */
	public static boolean isClimbableBlock(Block b) {
		String block = Utility.getMaterialName(b.getLocation());
		return block.contains("LADDER") || block.contains("VINE");
	}

	/**
	 * Check if a player has Vehicles near
	 * 
	 * @param p
	 * @param range
	 * @return
	 */
	public static boolean hasVehicleNear(Player p, int range) {
		if (p.isInsideVehicle()) {
			return true;
		}
		for (Entity e : p.getNearbyEntities(range, range, range)) {
			if (e instanceof Vehicle) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasLivingEntityNear(Player p, int range) {
		for (Entity e : p.getNearbyEntities(range, range, range)) {
			if (e instanceof LivingEntity) {
				return true;
			}
		}
		return false;
	}

	public static Block getPlayerUnderBlock(Player p) {
		return p.getLocation().add(0, -0.7, 0).getBlock();
	}

	/**
	 * Gets the ping of a player reflectively
	 * 
	 * @param player the player
	 * @return the ping
	 * @deprecated Handles reflective operation exceptions badly.
	 */
	@Deprecated
	public static int getPing(final Player player) {
		int ping = 76;
		try {
			final Object entityPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception ex) {
		}
		return ping;
	}

	public static double getDistanceFromGround(final Location loc) {
		double dTG = 0; // Distance to ground
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				int y = 0;
				while (!loc.clone().subtract(x, y, z).getBlock().getType().isSolid() && y < 20) {
					y++;
				}
				if (y < dTG || dTG == 0)
					dTG = y;
			}
		}
		dTG += loc.getY() % 1;
		return dTG;
	}

	public static boolean groundAround(Location loc) {
		int radius = 2;
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					Block block = loc.getWorld().getBlockAt(loc.clone().add(x, y, z));
					if (block.getType().isSolid()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isOnGroundUsingCollider(Location loc, MaterialAccess access, Double d) {
		boolean onGround = false;
		for (Block b : getCollidingBlocks(loc, d, 0.1)) {
			Material material = access.getMaterial(b);
			boolean fence = material.name().contains("FENCE");
			boolean gate = material.name().contains("GATE");
			boolean walls = material.name().contains("WALLS");
			if (material.isSolid() || fence || gate || walls) {
				onGround = true;
			}
		}
		return onGround;
	}

	public static boolean isNearWater(Location loc, MaterialAccess access) {
		int water = 0;
		for (Block b : getCollidingBlocks(loc, 0.3, 0.1)) {
			String material = access.getMaterial(b).name();
			if (material.contains("WATER")) {
				water++;
			}
		}
		return water > 4;
	}

	public static boolean isNearLava(Location loc, MaterialAccess access) {
		int water = 0;
		for (Block b : getCollidingBlocks(loc, 0.5, 0.1)) {
			String material = access.getMaterial(b).name();
			if (material.contains("LAVA")) {
				water++;
			}
		}
		return water > 4;
	}

	/**
	 * Get Blocks near Location
	 * 
	 * @param Location loc
	 * @param double   distance indicates the border
	 * @param double   subtraction indicates what value should it subtract from Y
	 *                 coord
	 * @return List<Block> that contains colliding blocks
	 */
	public static List<Block> getCollidingBlocks(Location loc, Double distance, Double subtraction) {
		if (subtraction == null) {
			subtraction = 0.1;
		}
		List<Block> blocks = new ArrayList<Block>();
		final Location cloned = loc.clone().subtract(0, subtraction, 0);
		double limit;
		if (distance == null) {
			limit = 0.3;
		} else {
			limit = distance;
		}
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				blocks.add(cloned.clone().add(x, 0, z).getBlock());
			}
		}
		return blocks;
	}

	public static boolean isCollidingLadder(Location loc, MaterialAccess access) {
		for (Block b : getCollidingBlocks(loc, 0.2, 0.1)) {
			if(access.getMaterial(b).name().contains("LADDER") || access.getMaterial(b).name().contains("VINE")) {
				return true;
			}
		}
		return false;
	}

	public static List<Block> getBlocksAround(Location loc, int radius) {
		List<Block> result = new ArrayList<>();
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					result.add(loc.getWorld().getBlockAt(loc.clone().add(x, y, z)));
				}
			}
		}
		return result;
	}

	public static boolean hasflybypass(Player player) {
		if (!Bukkit.getVersion().contains("1.8")) {
			return player.hasPotionEffect(PotionEffectType.LEVITATION) || player.isGliding() || player.isFlying();
		} else {
			return player.isFlying();
		}
	}

	public static boolean hasBlock(Player p, String m) {
		m = m.toUpperCase();
		boolean done = false;
		Location loc = p.getLocation().clone();
		int min = (int) loc.getY() - 15;
		int max = (int) (loc.getY() + 15);
		if (min < 0) {
			min = 0;
		}
		if (max > 255) {
			max = 255;
		}
		for (int i = min; i < max; i++) {
			loc.setY(i);
			if (loc.getBlock().getType().name().contains(m)) {
				return true;
			}
		}
		return done;
	}

	public static Block getPlayerUpperBlock(Player p) {
		return p.getLocation().add(0, 1.9, 0).getBlock();
	}

	public static boolean specificBlockNear(Location loc, String m) {
		m = m.toUpperCase();
		for (Block b : Utility.getBlocksAround(loc, 2)) {
			if (m.equals("LIQUID")) {
				if (b.isLiquid()) {
					return true;
				}
			}
			if (b.getType().name().contains(m)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMathematicallyOnGround(double y) {
		return y % (1D / 64D) == 0;
	}

	public static double calcDamage(double dist) {
		return (dist * 0.5) - 1.5;
	}

	public static double getDistance3D(Location one, Location two) {
		double toReturn = 0.0D;
		double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
		double ySqr = (two.getY() - one.getY()) * (two.getY() - one.getY());
		double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
		double sqrt = Math.sqrt(xSqr + ySqr + zSqr);
		toReturn = Math.abs(sqrt);
		return toReturn;
	}

	public static int getPotionEffectLevel(Player p, PotionEffectType pet) {
		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().getName().equals(pet.getName())) {
				return pe.getAmplifier() + 1;
			}
		}
		return 0;
	}

	public static boolean isInWater(Player player) {
		final Material m = player.getLocation().getBlock().getType();
		return m.name().contains("WATER");
	}

	@Deprecated
	/**
	 * This check if the player has air around This needs a recode
	 * 
	 * @param player
	 * @return
	 */
	public static boolean hasKbBypass(Player player) {
		if (!player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(-1.0D, 0.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(0.0D, 0.0D, -1.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(1.0D, 1.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(0.0D, 1.0D, 1.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(-1.0D, 1.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(0.0D, 1.0D, -1.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(1.0D, 2.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(0.0D, 2.0D, 1.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(0.0D, 2.0D, -1.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(-1.0D, 2.0D, 0.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(1.0D, 2.0D, 1.0D).getBlock().getType().isSolid())
			return true;
		if (!player.getLocation().add(-1.0D, 2.0D, -1.0D).getBlock().getType().isSolid())
			return true;
		return player.getVehicle() != null;
	}

}
