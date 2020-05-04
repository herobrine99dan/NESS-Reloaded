package com.github.ness;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DragDown {

	protected static void PlayerDragDown(Player p) {
	      Location Locfrom = p.getLocation().clone();
	      Block b = Locfrom.clone().subtract(0.0D, 0.5D, 0.0D).getBlock();
	      if (!b.getType().isSolid()) {
	        Locfrom = Locfrom.subtract(0.0D, 0.5D, 0.0D);
	      }
	      p.teleport(Locfrom, PlayerTeleportEvent.TeleportCause.PLUGIN);
	      return;
	}	
}
