package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.ness.Utility;

public class Freecam {

	public static void Check(Player p) {
		if (!Utility.isOnGround(p)) {
			return;
		}
		Location loc = p.getLocation();
		//p.teleport(loc.add(0, 0.01, 0));
	}
}
