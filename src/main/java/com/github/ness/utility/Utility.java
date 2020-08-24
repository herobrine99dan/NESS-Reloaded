package com.github.ness.utility;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utility {
	

	/**
	 * Round a value
	 * @param value
	 * @param precision (For example 100 will round 0.25065 to 0.25)
	 * @return
	 */
	public static double round(double value, int precision) {
		return (double) Math.round(value * precision) / precision;
	}

	/**
	 * Check if a player is next to a wall
	 * 
	 * @param p
	 * @return
	 */
	public static boolean nextToWall(Player p) {
		Location xp = new Location(p.getWorld(), p.getLocation().getY() + 0.5, p.getLocation().getY(),
				p.getLocation().getZ());
		Location xn = new Location(p.getWorld(), p.getLocation().getY() + 0.5, p.getLocation().getY(),
				p.getLocation().getZ());
		Location zp = new Location(p.getWorld(), p.getLocation().getY() + 0.5, p.getLocation().getY(),
				p.getLocation().getZ());
		Location zn = new Location(p.getWorld(), p.getLocation().getY() + 0.5, p.getLocation().getY(),
				p.getLocation().getZ());
		Location xpl = new Location(p.getWorld(), p.getLocation().getY(), p.getLocation().getY(),
				p.getLocation().getZ());
		Location xnl = new Location(p.getWorld(), p.getLocation().getY(), p.getLocation().getY(),
				p.getLocation().getZ());
		Location zpl = new Location(p.getWorld(), p.getLocation().getY(), p.getLocation().getY(),
				p.getLocation().getZ());
		Location znl = new Location(p.getWorld(), p.getLocation().getY(), p.getLocation().getY(),
				p.getLocation().getZ());
		if (xp.getBlock().getType().isSolid() && xpl.getBlock().getType().isSolid()) {
		} else if (xn.getBlock().getType().isSolid() && xnl.getBlock().getType().isSolid()) {
			return true;
		} else if (zp.getBlock().getType().isSolid() && zpl.getBlock().getType().isSolid()) {
			return true;
		} else if (zn.getBlock().getType().isSolid() && znl.getBlock().getType().isSolid()) {
			return true;
		}
		return false;
	}

	/**
	 * Get the material name from the location and convert it to lowercase
	 * @param loc
	 * @return the material name
	 */
	public static String getMaterialName(Location loc) {
		return loc.getBlock().getType().name().toLowerCase();
	}

	/**
	 * Get the material name and convert it to lowercase
	 * @param loc
	 * @return the material name
	 */
	public static String getMaterialName(Material m) {
		return m.name().toLowerCase();
	}

	/**
	 * Check if a block is climbable
	 * @param b
	 * @return
	 */
	public static boolean isClimbableBlock(Block b) {
		String block = Utility.getMaterialName(b.getLocation());
		return block.contains("ladder") || block.contains("vine");
	}

	
	public static boolean isWeb(Location loc) {
		return Utility.getMaterialName(loc.clone().add(0, -0.2, 0)).contains("web");
	}

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

	public static int getPing(final Player player) {
		int ping = 900;
		try {
			final Object entityPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player,
					new Object[0]);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception ex) {
		}
		return ping;
	}

	public static void setPing(Player player, int ping) {
		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."
					+ Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".entity.CraftPlayer");
			Object handle = craftPlayerClass.getMethod("getHandle", new Class[0]).invoke(craftPlayerClass.cast(player),
					new Object[0]);
			handle.getClass().getDeclaredField("ping").set(handle, Integer.valueOf(50));
		} catch (Exception exception) {

		}
	}

	public static Integer calculateFallDistance(final Location loc) {
		Location res;
		int yDif;
		for (res = loc.clone(), yDif = -1; !res.subtract(0.0, yDif, 0.0).getBlock().getType().isSolid()
				&& res.subtract(0.0, yDif, 0.0).getY() > 0.0; ++yDif) {
			res.add(0.0, yDif, 0.0);
		}
		return yDif;
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

	public static List<Block> getBlocksAround(Location loc) {
		List<Block> result = new ArrayList<>();
		result.add(loc.getBlock());
		for (double y = -0.5; y < 1; y += 0.5) {
			result.add(loc.clone().add(0.5D, y, -0.5D).getBlock());
			result.add(loc.clone().add(0.5D, y, 0.0D).getBlock());
			result.add(loc.clone().add(-0.5D, y, 0.0D).getBlock());
			result.add(loc.clone().add(0.0D, y, -0.5D).getBlock());
			result.add(loc.clone().add(0.0D, y, 0.5D).getBlock());
			result.add(loc.clone().add(-0.5D, y, -0.5D).getBlock());
			result.add(loc.clone().add(0.5D, y, 0.5D).getBlock());
			result.add(loc.clone().add(-0.5D, y, 0.5D).getBlock());
		}
		return result;
	}

	public static boolean hasflybypass(Player player) {
		if (!Bukkit.getVersion().contains("1.8")) {
			if (player.hasPotionEffect(PotionEffectType.LEVITATION) || player.isGliding() || player.isFlying()) {
				return true;
			} else {
				return false;
			}
		} else {
			if (player.isFlying()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean hasBlock(Player p, String m) {
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
			if (loc.getBlock().getType().name().toLowerCase().contains(m)) {
				return true;
			}
		}
		return done;
	}

	public static Block getPlayerUpperBlock(Player p) {
		return p.getLocation().add(0, 1.9, 0).getBlock();
	}

	public static boolean specificBlockNear(Location loc, String m) {
		for (Block b : Utility.getBlocksAround(loc)) {
			if (m.equals("liquid")) {
				if (b.isLiquid()) {
					return true;
				}
			}
			if (b.getType().name().toLowerCase().contains(m.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMathematicallyOnGround(double y) {
		if (y % (1D / 64D) == 0) {
			return true;
		}
		return false;
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
				return pe.getAmplifier();
			}
		}
		return 0;
	}

	public static double around(double i, int places) {
		String around;
		try {
			if (Double.toString(i).length() > places - 2) {
				if (!Double.toString(i).contains("-")) {
					around = Double.toString(i).substring(0, places - 1);
				} else {
					around = Double.toString(i).substring(0, places);
				}
			} else {
				around = Math.round(i) + "";
			}
		} catch (Exception e) {
			return i;
		}
		return Double.parseDouble(around);
	}

	public static boolean isInWater(Player player) {
		final Material m = player.getLocation().getBlock().getType();
		return m.name().toLowerCase().contains("water");
	}

	public static boolean isOnSlime(Player p) {
		return Utility.getMaterialName(p.getLocation().add(0, -0.5, 0)).contains("slime");
	}

	public static float yawTo180F(float flub) {
		if ((flub %= 360.0f) >= 180.0f) {
			flub -= 360.0f;
		}
		if (flub < -180.0f) {
			flub += 360.0f;
		}
		return flub;
	}

	public static boolean isOnSpecificBlock(Player p, String type) {
		return Utility.getMaterialName(p.getLocation().add(0, -0.5, 0)).contains(type);
	}

	public static double getFraction(final double value) {
		return value % 1.0;
	}

	public static boolean hasKbBypass(Player player) {
		if (player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(1.0D, 0.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(0.0D, 0.0D, 1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(-1.0D, 0.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(0.0D, 0.0D, -1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(1.0D, 1.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(0.0D, 1.0D, 1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(-1.0D, 1.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(0.0D, 1.0D, -1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(0.0D, 2.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(1.0D, 2.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(0.0D, 2.0D, 1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(0.0D, 2.0D, -1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(-1.0D, 2.0D, 0.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(1.0D, 2.0D, 1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getLocation().add(-1.0D, 2.0D, -1.0D).getBlock().getType().name().toLowerCase().contains("air"))
			return true;
		if (player.getVehicle() != null)
			return true;
		return false;
	}

}
