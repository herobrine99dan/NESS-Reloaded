package com.github.ness;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class MSG {
	public static String color(final String msg) {
		if (msg == null) {
			return null;
		}
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String camelCase(final String string) {
		String prevChar = " ";
		String res = "";
		for (int i = 0; i < string.length(); ++i) {
			if (i > 0) {
				prevChar = new StringBuilder(String.valueOf(string.charAt(i - 1))).toString();
			}
			if (!prevChar.matches("[a-zA-Z]")) {
				res = String.valueOf(res)
						+ new StringBuilder(String.valueOf(string.charAt(i))).toString().toUpperCase();
			} else {
				res = String.valueOf(res)
						+ new StringBuilder(String.valueOf(string.charAt(i))).toString().toLowerCase();
			}
		}
		return res;
	}

	public static String parseDecimal(String name, final int length) {
		if (name.contains(".") && name.split("\\.")[1].length() > 2) {
			name = String.valueOf(name.split("\\.")[0]) + "."
					+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), length));
		}
		return name;
	}

	public static void tell(final CommandSender sender, final String msg) {
		if (msg != null) {
			sender.sendMessage(color(msg));
		}
	}

	public static void tell(final World world, final String msg) {
		if (world != null && msg != null) {
			for (final Player target : world.getPlayers()) {
				tell((CommandSender) target, msg);
			}
		}
	}

	public static void log(final String msg) {
		tell((CommandSender) Bukkit.getConsoleSender(), "[NESS] " + msg);
	}

	public static String TorF(final Boolean bool) {
		if (bool) {
			return "&aTrue&r";
		}
		return "&cFalse&r";
	}

	public static void noPerm(final CommandSender sender) {
		tell(sender, NESS.main.config.getString("Messages.NoPermission").replace("%prefix%", NESS.main.prefix));
	}

	public static String genUUID(final int length) {
		final String[] keys = new String[100];
		int pos = 0;
		for (int i = 0; i < 26; ++i) {
			keys[i + pos] = new StringBuilder(String.valueOf((char) (i + 65))).toString();
		}
		pos += 26;
		for (int i = 0; i < 10; ++i) {
			keys[i + pos] = new StringBuilder(String.valueOf(i)).toString();
		}
		pos += 10;
		String res = "";
		for (int j = 0; j < length; ++j) {
			res = String.valueOf(res) + keys[(int) Math.floor(Math.random() * pos)];
		}
		return res;
	}

	public static Boolean outdated(String oldVer, String newVer) {
		oldVer = oldVer.replace(".", "");
		newVer = newVer.replace(".", "");
		Double oldV = null;
		Double newV = null;
		try {
			oldV = Double.valueOf(oldVer);
			newV = Double.valueOf(newVer);
		} catch (Exception e) {
			if (NESS.main.config.getBoolean("CheckForUpdates")) {
				log("&cError! &7Versions incompatible.");
			}
			return false;
		}
		if (oldVer.length() > newVer.length()) {
			newV *= Math.pow(10.0, oldVer.length() - newVer.length());
		} else if (oldVer.length() < newVer.length()) {
			oldV *= Math.pow(10.0, newVer.length() - oldVer.length());
		}
		if (oldV < newV) {
			return true;
		}
		return false;
	}

	protected static String vlCol(Integer vl) {
		return "&7";
	}
}
