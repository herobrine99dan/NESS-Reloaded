package org.mswsplex.MSWS.NESS.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.Utility;

public class Freecam {

	public static void Check(Player p) {
		if (!Utility.isOnGround(p)) {
			return;
		}
		Location loc = p.getLocation();
		//p.teleport(loc.add(0, 0.01, 0));
	}
}
