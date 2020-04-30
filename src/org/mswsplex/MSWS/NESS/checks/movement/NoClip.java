package org.mswsplex.MSWS.NESS.checks.movement;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class NoClip {

	public static void Check(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final Location from = event.getFrom();
		final Location to = event.getTo();
		final Double dist = from.distance(to);
		Double hozDist = dist - (to.getY() - from.getY());
		boolean surrounded = true;
		for (int x2 = -2; x2 <= 2; ++x2) {
			for (int y = -2; y <= 3; ++y) {
				for (int z3 = -2; z3 <= 2; ++z3) {
					final Material belowSel2 = player.getWorld()
							.getBlockAt(player.getLocation().add((double) x2, (double) y, (double) z3)).getType();
					if (!belowSel2.isSolid()) {
						surrounded = false;
					}
				}
			}
		}
		if (surrounded && (hozDist > 0.2 || to.getBlockY() < from.getBlockY())) {
			WarnHacks.warnHacks(player, "NoClip", 10, -1.0, 21,"Passable",false);
		}

	}

}
