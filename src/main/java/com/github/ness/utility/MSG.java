package com.github.ness.utility;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MSG {
	public static String color(String msg) {
		if (msg == null)
			return null;
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String camelCase(String string) {
		String prevChar = " ";
		String res = "";
		for (int i = 0; i < string.length(); i++) {
			if (i > 0)
				prevChar = string.charAt(i - 1) + "";
			if (!prevChar.matches("[a-zA-Z]")) {
				res = res + ((string.charAt(i) + "").toUpperCase());
			} else {
				res = res + ((string.charAt(i) + "").toLowerCase());
			}
		}
		return res;
	}
	
	public static String parseDecimal(String name, int length) {
		if (name.contains(".")) {
			if (name.split("\\.")[1].length() > 2) {
				name = name.split("\\.")[0] + "."
						+ name.split("\\.")[1].substring(0, Math.min(name.split("\\.")[1].length(), length));
			}
		}
		return name;
	}

	public static void tell(CommandSender sender, String msg) {
		if (msg != null)
			sender.sendMessage(color(msg));
	}

	public static void tell(World world, String msg) {
		if (world != null && msg != null) {
			for (Player target : world.getPlayers()) {
				tell(target, msg);
			}
		}
	}

	public static void log(String msg) {
		MSG.tell(Bukkit.getConsoleSender(), "[NESS] " + msg);
	}

	public static String TorF(Boolean bool) {
		if (bool) {
			return "&aTrue&r";
		} else {
			return "&cFalse&r";
		}
	}

	public static String genUUID(int length) {
		String[] keys = new String[100];
		int pos = 0;
		for (int i = 0; i < 26; i++) {
			keys[i + pos] = ((char) (i + 65)) + "";
		}
		pos += 26;
		for (int i = 0; i < 10; i++) {
			keys[i + pos] = i + "";
		}
		pos += 10;
		String res = "";
		for (int i = 0; i < length; i++)
			res = res + keys[(int) Math.floor(Math.random() * pos)];
		return res;
	}
}
