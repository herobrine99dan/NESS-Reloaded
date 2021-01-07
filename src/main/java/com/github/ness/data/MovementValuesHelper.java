package com.github.ness.data;

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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.utility.Utility;

public class MovementValuesHelper {

	private final boolean vehicleNear;
	private final boolean livingEntityNear;

	public MovementValuesHelper(boolean vehicleNear, boolean livingEntityNear) {
		this.vehicleNear = vehicleNear;
		this.livingEntityNear = livingEntityNear;
	}

	/**
	 * Get Angle (in Radians) beetween a player and the target
	 * 
	 * @param player
	 * @param target
	 * @param direction (if it is null, will be replaced with player direction)
	 * @return the angle in radians
	 */
	public double getAngle(Player player, Location target, Vector direction) {
		Location eye = player.getEyeLocation();
		if (direction == null) {
			direction = player.getLocation().getDirection();
		}
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(direction);
		return dot;
	}

	public boolean groundAround(Location loc) {
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

	public List<Block> getCollidingBlocks(Location loc, Double distance, Double subtraction) {
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
	
	public boolean isMathematicallyOnGround(double y) {	
		return y % (1D / 64D) == 0;	
	}
	
	public boolean hasflybypass(Player player) {
		if (!Bukkit.getVersion().contains("1.8")) {
			return player.hasPotionEffect(PotionEffectType.LEVITATION) || player.isGliding() || player.isFlying();
		} else {
			return player.isFlying();
		}
	}
	
	public boolean specificBlockNear(Location loc, String m) {
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
	
	public double calcDamageFall(double dist) {
		return (dist * 0.5) - 1.5;
	}
	
	public boolean isOnGroundUsingCollider(Location loc, MaterialAccess access) {
		final double limit = 0.5;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				Block block = loc.clone().add(x, 0, z).getBlock();
				Material material = access.getMaterial(block);
				if (material.isSolid()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasBlock(Player p, String m) {
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

	public static MovementValuesHelper makeHelper(MovementValues values) {
		boolean vehicleNear = false;
		boolean livingEntityNear = true;
		final int range = 4;
		for (Entity e : values.getPlayer().getNearbyEntities(range, range, range)) {
			if (e instanceof Vehicle) {
				vehicleNear = true;
			}
			if (e instanceof LivingEntity && !(e instanceof Player)) {
				livingEntityNear = true;
			}
		}
		MovementValuesHelper helper = new MovementValuesHelper(vehicleNear, livingEntityNear);
		return helper;
	}

	@SuppressWarnings("deprecation")
	public boolean isItemInHand(Player p, String m) {
		if (Bukkit.getVersion().contains("1.8")) {
			return p.getItemInHand().getType().name().contains(m); // 1.8 Version doesn't have the second hand!
		} else {
			PlayerInventory inventory = p.getInventory();
			return inventory.getItemInMainHand().getType().name().contains(m)
					|| inventory.getItemInOffHand().getType().name().contains(m);
		}
	}

	public boolean isVehicleNear() {
		return vehicleNear;
	}

	public boolean isLivingEntityNear() {
		return livingEntityNear;
	}

}
