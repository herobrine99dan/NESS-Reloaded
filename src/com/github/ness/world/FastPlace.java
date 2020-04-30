package com.github.ness.world;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NESSPlayer;
import com.github.ness.WarnHacks;

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
