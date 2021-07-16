package com.github.ness.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.blockgetter.MaterialAccess;

public class MovementValuesHelper {

	private final MaterialAccess materialAccess;

	public MovementValuesHelper(MaterialAccess materialAccess) {
		this.materialAccess = materialAccess;
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

	public Location getEyeLocation(Location location, Player player) {
		return location.add(0, player.getEyeHeight(), 0);
	}

	public boolean isNearWater(Location loc) {
		int water = 0;
		double limit = 0.22;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				if (materialAccess.getMaterial(loc.clone().add(x, 0, z)).name().contains("WATER")) {
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
				final Material material = materialAccess.getMaterial(loc.clone().add(x, 0, z));
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
				if (materialAccess.getMaterial(loc.clone().add(x, 0, z)).name().contains("LAVA")) {
					water++;
				}
			}
		}
		return water > 3;
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

	public double calcDamageFall(double dist) {
		return (dist * 0.5) - 1.5;
	}

	private static final float ONGROUNDBOUNDINGBOXWITDH = 0.4f;
	private static final float ONGROUNDBOUNDINGBOXHEIGHT = 0.7f;

	/**
	 * A Good isOnGround Method
	 * 
	 * @param Location           loc [the location that we have to check]
	 * @param otherBlocksToCheck [some other blocks that you consider onGround]
	 *                           (some checks may need this)
	 * @return
	 */
	public boolean isOnGround(Location loc, String... otherBlocksToCheck) {
		final Location cloned = loc.clone();
		double limit = ONGROUNDBOUNDINGBOXWITDH;
		for (double x = -limit; x < limit; x += 0.05) {
			for (double z = -limit; z < limit; z += 0.05) {
				for (double y = -ONGROUNDBOUNDINGBOXHEIGHT; y < 0; y += 0.05) {
					if (isBlockConsideredOnGround(cloned.clone().add(x, y, z), otherBlocksToCheck)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public List<Location> getBoundingBoxesAroundPlayer(Location loc) {
		List<Location> list = new ArrayList<Location>();
		final Location cloned = loc.clone();
		double limit = ONGROUNDBOUNDINGBOXWITDH;
		for (double x = -limit; x < limit; x += 0.05) {
			for (double z = -limit; z < limit; z += 0.05) {
				for (double y = -ONGROUNDBOUNDINGBOXHEIGHT; y < 0; y += 0.05) {
					list.add(cloned.clone().add(x, y, z));
				}
			}
		}
		return list;
	}

	public boolean isNearMaterials(Location loc, String... materials) {
		for(String s : materials) {
			if(isNearMaterial(loc, s)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isNearMaterial(Location loc, String material) {
		for(Location newLoc : getBoundingBoxesAroundPlayer(loc)) {
			if(this.materialAccess.getMaterial(newLoc).name().contains(material)) return true;
		}
		//return getBoundingBoxesAroundPlayer(loc).stream()
		//		.anyMatch((newLoc) -> this.materialAccess.getMaterial(newLoc).name().contains(material));
		return false;
	}

	public boolean isBlockConsideredOnGround(Location loc, String... otherBlocksToCheck) {
		Block block = loc.getBlock();
		Material material = this.materialAccess.getMaterial(block);
		String name = material.name();
		if (material.isSolid() || name.contains("SNOW") || name.contains("CARPET") || name.contains("SCAFFOLDING")
				|| name.contains("SKULL") || name.contains("LADDER") || name.contains("WALL")
				|| elementOfStringContainsMaterial(name, otherBlocksToCheck)) {
			return true;
		}
		return false;
	}
	
	private boolean elementOfStringContainsMaterial(String material, String... otherBlocksToCheck) {
		for(String s : otherBlocksToCheck) {
			if(s.contains(material)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCollidedHorizontally(Location loc) {
		final Location cloned = loc.clone();
		double limit = 0.1;
		for (double x = -limit; x < limit + 0.1; x += limit) {
			for (double z = -limit; z < limit + 0.1; z += limit) {
				Block block = cloned.clone().add(x, 0, z).getBlock();
				Material material = materialAccess.getMaterial(block);
				if (material.isSolid()) {
					return true;
				}
			}
		}
		return false;
	}

	public static MovementValuesHelper makeHelper(MaterialAccess materialAccess) {
		return new MovementValuesHelper(materialAccess);
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

	public MaterialAccess getMaterialAccess() {
		return materialAccess;
	}
}
