package com.github.ness.utility;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.data.ImmutableLoc;

public class Utility {

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

	public static boolean hasVehicleNear(Player player) {
		final int range = 4;
		for (Entity e : player.getNearbyEntities(range, range, range)) {
			if (e instanceof Vehicle) {
				return true;
			}
		}
		return false;
	}

	public static float[] getRotationFromPosition(Location playerLocation, Location targetLocation) {
		double xDiff = targetLocation.getX() - playerLocation.getX();
		double zDiff = targetLocation.getZ() - playerLocation.getZ();
		double yDiff = targetLocation.getY() - playerLocation.getY() + 0.2F;
		double dist = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 45.0F;
		float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
		return new float[] { yaw, pitch };
	}

	public static boolean hasLivingEntityNear(Player player) {
		final int range = 4;
		for (Entity e : player.getNearbyEntities(range, range, range)) {
			if (e instanceof LivingEntity && !e.getUniqueId().equals(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	public static int getPotionEffectLevel(Player p, PotionEffectType pet) {
		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getType().getName().equals(pet.getName())) {
				return pe.getAmplifier() + 1;
			}
		}
		return 0;
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
