package com.github.ness;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DragDown {

	/**
	 * DragDown method to teleport the player down
	 * 
	 * @param player the corresponding player
	 */

	public static void PlayerDragDown(Player p,PlayerMoveEvent e) {
		try {
			Location Locfrom = p.getLocation().clone();
			Block b = Locfrom.clone().subtract(0.0D, 0.3D, 0.0D).getBlock();
			if (!b.getType().isSolid()) {
				Locfrom = Locfrom.subtract(0.0D, 0.3D, 0.0D);
			}
			p.teleport(Locfrom, PlayerTeleportEvent.TeleportCause.PLUGIN);
		}catch(Exception ex) {
			e.setCancelled(true);
		}
		return;
	}
}
