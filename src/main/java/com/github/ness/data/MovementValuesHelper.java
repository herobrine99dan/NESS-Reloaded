package com.github.ness.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.utility.Utility;

public class MovementValuesHelper {

	private final MaterialAccess access;

	public MovementValuesHelper(MaterialAccess access) {
		this.access = access;
	}

	/**
	 * Get Angle (in Radians) beetween a player and the target
	 * 
	 * @param player
	 * @param target
	 * @param direction (if it is null, will be replaced with player direction)
	 * @return the angle in radians
	 */
	public double getAngle(NessPlayer player, Location target) {
		Location eye = getEyeLocation(player.getMovementValues().getTo().toBukkitLocation(), player.getBukkitPlayer());
		Vector direction = player.getMovementValues().getDirection();
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(direction);
		return dot;
	}

	/**
	 * Get Angle (in Radians) beetween a player and the target
	 * 
	 * @param player
	 * @param target
	 * @param direction (if it is null, will be replaced with player direction)
	 * @return the angle in radians
	 */
	public double getAngle(Player player, Location target) {
		Location eye = player.getEyeLocation();
		Vector direction = player.getLocation().getDirection();
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(direction);
		return dot;
	}

	/**
	 * Get Angle (in Radians) beetween a player and the target
	 * 
	 * @param player
	 * @param target
	 * @param direction (if it is null, will be replaced with player direction)
	 * @return the angle in radians
	 */
	public double getAngle(NessPlayer player, LivingEntity target) {
		Location eye = getEyeLocation(player.getMovementValues().getTo().toBukkitLocation(), player.getBukkitPlayer());
		Vector direction = player.getMovementValues().getDirection();
		Vector toEntity = target.getLocation().add(0, target.getEyeHeight() / 2, 0).toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(direction);
		return dot;
	}

	public Location getEyeLocation(Location location, Player player) {
		return location.add(0, player.getEyeHeight(), 0);
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

	public List<Block> getCollidingBlocks(Location loc) {
		List<Block> blocks = new ArrayList<Block>();
		final Location cloned = loc.clone();
		double limit = 0.3;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				for (double y = -0.1; y < 1.99; y += 0.1) {
					blocks.add(cloned.clone().add(x, y, z).getBlock());
				}
			}
		}
		return blocks;
	}

	public boolean isNearWater(Location loc) {
		int water = 0;
		double limit = 0.22;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				if (access.getMaterial(loc.clone().add(x, 0, z)).name().contains("WATER")) {
					water++;
				}
			}
		}
		return water > 3;
	}

	public boolean isNearLiquid(Location loc) {
		int water = 0;
		double limit = 0.22;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				final Material material = access.getMaterial(loc.clone().add(x, 0, z));
				if (!material.isSolid()) {
					if (material.name().contains("WATER") || material.name().contains("LAVA")) {
						water++;
					}
				}
			}
		}
		return water > 3;
	}

	public boolean isNearLava(Location loc) {
		int water = 0;
		double limit = 0.22;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
					if (access.getMaterial(loc.clone().add(x, 0, z)).name().contains("LAVA")) {
						water++;
					}
			}
		}
		return water > 3;
	}

	public boolean collideLadders(Location loc) {
		for (Block block : getCollidingBlocks(loc)) {
			if (block.getType().name().contains("LADDER") || block.getType().name().contains("VINE")) {
				return true;
			}
		}
		return false;
	}

	public boolean isMathematicallyOnGround(double y) {
		return y % (1D / 64D) < 0.0001;
	}

	public boolean hasflybypass(NessPlayer nessPlayer) {
		Player player = nessPlayer.getBukkitPlayer();
		if (!Bukkit.getVersion().contains("1.8")) {
			return player.hasPotionEffect(PotionEffectType.LEVITATION) || player.isGliding() || player.isFlying()
					|| nessPlayer.milliSecondTimeDifference(PlayerAction.GLIDING) < 1500;
		} else {
			return player.isFlying();
		}
	}

	public boolean isPlayerUsingElytra(NessPlayer nessPlayer) {
		Player player = nessPlayer.getBukkitPlayer();
		if (!Bukkit.getVersion().contains("1.8")) {
			return player.isGliding() || nessPlayer.milliSecondTimeDifference(PlayerAction.GLIDING) < 1500;
		}
		return false; // IN 1.8 there isn't Elytra!
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

	public boolean isOnGroundUsingCollider(Location loc) {
		final Location cloned = loc.clone();
		double limit = 0.3;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				Block block = cloned.clone().add(x, -0.35, z).getBlock();
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

	public static MovementValuesHelper makeHelper(MaterialAccess access) {
		return new MovementValuesHelper(access);
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
}
