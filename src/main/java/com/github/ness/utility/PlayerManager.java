package com.github.ness.utility;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerManager {
	
	static HashMap<String,Long> wasonground = new HashMap<>();
	static HashMap<String,Long> wasice = new HashMap<>();

	
	public static boolean groundAround(Location loc) {
		int radius = 2;
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					Material mat = loc.getWorld().getBlockAt(loc.add(x, y, z)).getType();
					if (mat.isSolid() || mat == Material.WATER || mat == Material.STATIONARY_WATER
							|| mat == Material.LAVA || mat == Material.STATIONARY_LAVA) {
						loc.subtract(x, y, z);
						return true;
					}
					loc.subtract(x, y, z);
				}
			}
		}
		return false;
	}

	public static boolean redstoneAround(Location loc) {
		int radius = 2;
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					Material mat = loc.getWorld().getBlockAt(loc.add(x, y, z)).getType();
					if (mat.toString().toLowerCase().contains("piston")) {
						loc.subtract(x, y, z);
						return true;
					}
					loc.subtract(x, y, z);
				}
			}
		}
		return false;
	}

	public static int getPing(Player player) {
		int ping = 999;
		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception e) {
		}
		return ping;
	}

	public static Integer distToBlock(Location loc) {
		Location res = loc;
		int yDif = -1;
		while (!res.subtract(0, yDif, 0).getBlock().getType().isSolid() && res.subtract(0, yDif, 0).getY() > 0) {
			res.add(0, yDif, 0);
			yDif++;
		}
		return yDif;
	}

	public static void setAction(String category, Player player, Long amo) {
		if(category.equals("wasGround")) {
			wasonground.put(player.getName(), amo);
		}else if(category.equals("wasIce")) {
			wasice.put(player.getName(), amo);
		}
	}

	public static Long getAction(String category, Player player) {
		if(category.equals("wasGround")) {
			return wasonground.getOrDefault(player.getName(), (long) 0);
		}else if(category.equals("wasIce")) {
			return wasice.getOrDefault(player.getName(), (long) 0);
		}else {
			return (long) 0;
		}
	}

	public static Long timeSince(String category, Player player) {
		return (System.currentTimeMillis() - getAction(category, player));
	}
	
}
