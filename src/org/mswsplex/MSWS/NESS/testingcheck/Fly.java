package org.mswsplex.MSWS.NESS.testingcheck;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.Utility;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class Fly {

	public static void Check(PlayerMoveEvent event) {
		Player player = event.getPlayer();
	      if (!player.isFlying() && !Utility.blockAdjacentIsLiquid(player.getLocation()) && Utility.isOnGround(player)) {
	         double offsetH = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());
	         double offsetY = event.getTo().getY() - event.getFrom().getY();
	         if (offsetH > 0.0D && offsetY == 0.0D) {
	        	 WarnHacks.warnHacks(event.getPlayer(), "Fly", 5, -1.0D, 1, "AimAssistA", false);
	         }
	      } 
	}
	
}
