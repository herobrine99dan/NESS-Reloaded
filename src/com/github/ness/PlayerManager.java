
package com.github.ness;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerManager {
	public static boolean groundAround(final Location loc) {
		for (int radius = 2, x = -radius; x < radius; ++x) {
			for (int y = -radius; y < radius; ++y) {
				for (int z = -radius; z < radius; ++z) {
					final Material mat = loc.getWorld().getBlockAt(loc.add((double) x, (double) y, (double) z))
							.getType();
					if (mat.isSolid() || mat.name().toLowerCase().contains("lava")
							|| mat.name().toLowerCase().contains("water")) {
						loc.subtract((double) x, (double) y, (double) z);
						return true;
					}
					loc.subtract((double) x, (double) y, (double) z);
				}
			}
		}
		return false;
	}

	public static boolean redstoneAround(final Location loc) {
		for (int radius = 2, x = -radius; x < radius; ++x) {
			for (int y = -radius; y < radius; ++y) {
				for (int z = -radius; z < radius; ++z) {
					final Material mat = loc.getWorld().getBlockAt(loc.add((double) x, (double) y, (double) z))
							.getType();
					if (mat.toString().toLowerCase().contains("piston")) {
						loc.subtract((double) x, (double) y, (double) z);
						return true;
					}
					loc.subtract((double) x, (double) y, (double) z);
				}
			}
		}
		return false;
	}
	
	public static boolean hasPermissionBypass(Player p, String hack) {
		return p.hasPermission("ness.bypass." + hack) || p.hasPermission("ness.bypass.*");
	}

	public static void resetVL(final OfflinePlayer player, final String hack) {
		if (hack.equalsIgnoreCase("all")) {
			NESS.main.vl.set(new StringBuilder().append(player.getUniqueId()).toString(), (Object) null);
		} else {
			NESS.main.vl.set(player.getUniqueId() + "." + hack, (Object) null);
		}
	}

	public static void addVL(final Player player, final String check, final int vl) {
		final String uuid = new StringBuilder().append(player.getUniqueId()).toString();
		NESS.main.vl.set(String.valueOf(uuid) + "." + check,
				(Object) (NESS.main.vl.getInt(String.valueOf(uuid) + "." + check) + vl));
		if (NESS.main.vl.getInt(String.valueOf(uuid) + "." + check) <= 0) {
			NESS.main.vl.set(String.valueOf(uuid) + "." + check, (Object) null);
		}
		if (NESS.main.vl.getConfigurationSection(uuid).getKeys(false).size() == 0) {
			NESS.main.vl.set(uuid, (Object) null);
		}
		NESS.main.saveVl();
	}

	public static Integer getVl(final OfflinePlayer player, final String check) {
		return NESS.main.vl.getInt(player.getUniqueId() + "." + check);
	}

	public static int getPing(final Player player) {
		int ping = 900;
		try {
			final Object entityPlayer = player.getClass().getMethod("getHandle", (Class<?>[]) new Class[0])
					.invoke(player, new Object[0]);
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

	public static Integer distToBlock(final Location loc) {
		Location res;
		int yDif;
		for (res = loc, yDif = -1; !res.subtract(0.0, (double) yDif, 0.0).getBlock().getType().isSolid()
				&& res.subtract(0.0, (double) yDif, 0.0).getY() > 0.0; ++yDif) {
			res.add(0.0, (double) yDif, 0.0);
		}
		return yDif;
	}

	public static void addAction(final String category, final Player player) {
		NESS.main.vl.set(player.getUniqueId() + ".category." + category, (Object) (getAction(category, player) + 1.0));
	}

	public static void setAction(final String category, final Player player, final Double amo) {
		NESS.main.vl.set(player.getUniqueId() + ".category." + category, (Object) amo);
	}

	public static void setInfo(final String category, final OfflinePlayer player, final Object obj) {
		NESS.main.vl.set(player.getUniqueId() + ".category." + category, obj);
	}

	public static Object getInfo(final String category, final OfflinePlayer player) {
		return NESS.main.vl.get(player.getUniqueId() + ".category." + category);
	}

	public static Double getAction(final String category, final Player player) {
		if (NESS.main.vl.contains(player.getUniqueId() + ".category." + category)) {
			return NESS.main.vl.getDouble(player.getUniqueId() + ".category." + category);
		}
		return 0.0;
	}

	public static void removeAction(final String category, final Player player) {
		NESS.main.vl.set(player.getUniqueId() + ".category." + category, (Object) null);
	}

	public static Double timeSince(final String category, final Player player) {
		return System.currentTimeMillis() - getAction(category, player);
	}

	public static Player getPlayer(final CommandSender sender, final String name) {
		final List<Player> list = new ArrayList<Player>();
		for (final Player target : Bukkit.getOnlinePlayers()) {
			if (target.getName().toLowerCase().contains(name.toLowerCase())) {
				list.add(target);
			}
		}
		if (list.size() != 1) {
			MSG.tell(sender, String.valueOf(NESS.main.prefix) + " &e" + list.size() + " &7players with \"&e" + name
					+ "&7\" in their username.");
			String msg = "";
			for (int i = 0; i < list.size(); ++i) {
				msg = String.valueOf(msg) + "&e" + list.get(i) + "&7, ";
			}
			if (list.size() > 0) {
				MSG.tell(sender, String.valueOf(NESS.main.prefix) + "Results: [" + msg + "&7].");
			}
			return null;
		}
		return list.get(0);
	}

	@SuppressWarnings("unchecked")
	public static void addLogMessage(final OfflinePlayer player, final String msg) {
		List<String> log;
		if (getInfo("log", player) == null) {
			log = new ArrayList<String>();
			log.add("BEGIN LOG User: " + player.getName() + " TIME:" + System.currentTimeMillis());
			setInfo("logStart", player, System.currentTimeMillis());
		} else {
			log = (List<String>) getInfo("log", player);
		}
		final double time = (double) (System.currentTimeMillis() - (long) getInfo("logStart", player));
		log.add(msg.replace("%time%", TimeManagement.getTime(time)));
		setInfo("log", player, log);
	}
}
