package com.github.ness.nms;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.ness.MovementPacketListener;

public class MovementPacketEvent {
	static private HashMap<String, Location> from = new HashMap<String, Location>();
	static private HashMap<String, Location> to = new HashMap<String, Location>();

	public static void execute(Location loc, Player sender) {
		from.remove(sender.getName());
		from.put(sender.getName(), to.getOrDefault(sender.getName(), loc));
		// the code
		MovementPacketListener mp = new MovementPacketListener();
		to.remove(sender.getName());
		to.put(sender.getName(), loc);
	}

	public static Location getFrom(Player p) {
		return from.get(p.getName());
	}

	public static Location getTo(Player p) {
		return to.get(p.getName());
	}

	public static void setTo(Player p, Location loc) {
		to.remove(p.getName());
		to.put(p.getName(), loc);
	}

	public static void setFrom(Player p, Location loc) {
		from.remove(p.getName());
		from.put(p.getName(), loc);
	}

}
