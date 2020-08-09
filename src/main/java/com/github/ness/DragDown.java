package com.github.ness;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

public class DragDown {

	private DragDown() {

	}

	/**
	 * DragDown method to teleport the player down
	 * 
	 * @param player the corresponding player
	 */

	public static void playerDragDown(PlayerMoveEvent e) {
		try {
			new BukkitRunnable() {
				@Override
				public void run() {
					Location loc = e.getTo().clone().add(0, -0.5, 0);
					if (!loc.getBlock().getType().isSolid()
							&& !NESSAnticheat.getInstance().getCheckManager().getPlayer(e.getPlayer()).isTeleported()) {
						e.getPlayer().teleport(loc, TeleportCause.PLUGIN);
					}
					return;
				}
			}.runTask(NESSAnticheat.getInstance());
			// p.teleport(Locfrom, PlayerTeleportEvent.TeleportCause.PLUGIN);
		} catch (Exception ex) {
			e.setCancelled(true);
		}
	}
}
