package com.github.ness.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

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

	public static MovementValuesHelper makeHelper(MovementValues values) {
		boolean vehicleNear = false;
		boolean livingEntityNear = true;
		final int range = 4;
		for (Entity e : values.getPlayer().getNearbyEntities(range, range, range)) {
			if (e instanceof Vehicle) {
				vehicleNear = true;
			}
			if (e instanceof LivingEntity) {
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
