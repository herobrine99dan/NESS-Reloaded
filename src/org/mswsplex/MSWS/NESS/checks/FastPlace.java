package org.mswsplex.MSWS.NESS.checks;

import org.bukkit.event.block.BlockPlaceEvent;
import org.mswsplex.MSWS.NESS.NESSPlayer;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class FastPlace {

	public static void Check(BlockPlaceEvent event) {
		NESSPlayer player = NESSPlayer.getInstance(event.getPlayer());
		player.SetBlockPlace(player.getBlockPlace()+1);
		if(player.getBlockPlace()>5) {
			WarnHacks.warnHacks(player.getPlayer(), "AutoClicker", 10, -1.0D, 10, "FastPlace", false);
			event.setCancelled(true);
		}
		
	}

}
