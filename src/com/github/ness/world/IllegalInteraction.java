package com.github.ness.world;

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.MSG;
import com.github.ness.NESS;
import com.github.ness.PlayerManager;
import com.github.ness.WarnHacks;

public class IllegalInteraction {

	public static void Check(BlockBreakEvent event) {
		final Player player = event.getPlayer();
		if (event.getBlock().isLiquid()) {
			WarnHacks.warnHacks(player, "Illegal Interaction", 100, 500.0, 6,"Liquids",false);
		}
		if (event.getBlock().getType() == Material.LONG_GRASS) {
			PlayerManager.setAction("longBroken", player, Double.valueOf(System.currentTimeMillis()));
		}
	}

	public static void Check1(BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		if (event.getBlockAgainst().isLiquid() && event.getBlock().getType() != Material.WATER_LILY) {
			punish(player,2,"LiquidInteraction");
		}
	}
	
	private static void punish(Player p,int i,String module) {
		WarnHacks.warnHacks(p, "Illegal Interaction", 100, 500.0, i,module,false);
	}

}
