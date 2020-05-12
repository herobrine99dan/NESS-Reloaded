package com.github.ness.nms;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.ness.MovementPacketEvent;

public class MovementPacketHelper {
	static private HashMap<String, Location> from = new HashMap<String, Location>();
	static private HashMap<String, Location> to = new HashMap<String, Location>();

	public static void execute(Location loc, Player sender) {
		from.remove(sender.getName());
		from.put(sender.getName(), to.getOrDefault(sender.getName(), loc));//here we store the old location
		MovementPacketEvent mp = new MovementPacketEvent(sender,from.get(sender.getName()),to.get(sender.getName())); //this is a small implementation of this system
		to.remove(sender.getName());
		to.put(sender.getName(), loc);//here we store the new location
	}
}
