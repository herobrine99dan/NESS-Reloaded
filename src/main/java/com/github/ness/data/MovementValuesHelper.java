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
	//TODO Recode this method
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

	public boolean isNearWater(Location loc) {
		for (Location newLoc : getLocationsAroundPlayerLocation(loc)) {
			if (materialAccess.getMaterial(newLoc).name().contains("WATER")) {
				return true;
			}
		}
		return false;
	}

	public boolean isNearLiquid(Location loc) {
		for (Location newLoc : getLocationsAroundPlayerLocation(loc)) {
			if (newLoc.getBlock().isLiquid()) {
				return true;
			}
		}
		return false;
	}

	public boolean isNearLava(Location loc) {
		for (Location newLoc : getLocationsAroundPlayerLocation(loc)) {
			if (materialAccess.getMaterial(newLoc).name().contains("LAVA")) {
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
			return player.hasPotionEffect(PotionEffectType.LEVITATION) || player.isFlying()
					|| isPlayerUsingElytra(nessPlayer);
		}
		return player.isFlying();
	}

//TODO See if some checks use this without checking the Server version
	/**When we use this method, we assume we already checked the Server Version**/
	private boolean isPlayerUsingElytra(NessPlayer nessPlayer) {
		Player player = nessPlayer.getBukkitPlayer();
		return player.isGliding() || nessPlayer.milliSecondTimeDifference(PlayerAction.GLIDING) < 1500;
	}

	public double calcDamageFall(double dist) {
		return (dist * 0.5) - 1.5;
	}

	/**
	 * A Good isOnGround Method
	 * 
	 * @param Location           loc [the location that we have to check]
	 * @return
	 */
	public boolean isOnGround(Location loc) {
		for (Location newLoc : getLocationsAroundPlayerLocation(loc)) {
			if (isBlockConsideredOnGround(newLoc))
						return true;
		}
		return false;
	}

	public List<Location> getLocationsAroundPlayerLocation(Location loc) {
		List<Location> list = new ArrayList<Location>();
		final double limit = 0.3;
		for (double x = -limit; x <= limit; x += limit) {
			for (double z = -limit; z <= limit; z += limit) {
				list.add(loc.clone().add(x, -0.501, z));
			}
		}
		return list;
	}

	public boolean isNearMaterials(Location loc, String... materials) {
		for(Location newLoc : getLocationsAroundPlayerLocation(loc)) {
			for(String s : materials) {
				if(this.materialAccess.getMaterial(newLoc).name().contains(s)) return true;
			}
		}
		return false;
	}

	public boolean isBlockConsideredOnGround(Location loc) {
		Block block = loc.getBlock();
		Material material = this.materialAccess.getMaterial(block);
		String name = material.name();
		
		if (material.isSolid() || name.contains("SNOW") || name.contains("CARPET") || name.contains("SCAFFOLDING")
				|| name.contains("SKULL") || name.contains("LADDER") || name.contains("WALL")) {
			return true;
		}
		return false;
	}
	
	//Bad and useless
	/*public boolean isCollidedHorizontally(Location loc) {
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
	}*/

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
