package com.github.ness;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class DragDown {

	/**
	 * DragDown method to teleport the player down
	 * 
	 * @param player the corresponding player
	 */

	public static boolean PlayerDragDown(Player p) {
		try {
			NESSAnticheat.main.getServer().getScheduler().scheduleSyncDelayedTask(NESSAnticheat.main, new Runnable() {
				@Override
				public void run() {
					Location loc = p.getLocation().clone().add(0, -0.5, 0);
					if (!loc.getBlock().getType().isSolid()) {
						p.teleport(loc, TeleportCause.PLUGIN);
					}
				}
			});
			// p.teleport(Locfrom, PlayerTeleportEvent.TeleportCause.PLUGIN);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
