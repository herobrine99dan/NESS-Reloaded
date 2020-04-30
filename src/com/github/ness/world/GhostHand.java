package com.github.ness.world;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NESS;
import com.github.ness.NESSPlayer;
import com.github.ness.WarnHacks;

public class GhostHand {

	public static void Check(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		final Location loc = player.getLocation();
		Block targetBlock = player.getTargetBlock((Set<Material>) null, 7);
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		if (!targetBlock.getType().isSolid())
			return;
		if (targetBlock.equals(event.getClickedBlock())) {
			return;
		}
		NESSPlayer p = NESSPlayer.getInstance(player);
		if(p.getDistance()>0.0 || p.getYawDelta()>0.0) {
			return;
		}
		if(targetBlock.getLocation().add(0, 1, 0).getBlock().getType().name().contains("Slab")) {
			return;
		}
		Location block = event.getClickedBlock().getLocation().add(event.getBlockFace().getModX(),
				event.getBlockFace().getModY(), event.getBlockFace().getModZ());
		Bukkit.getScheduler().runTaskLater(NESS.main, new Runnable() {
			public void run() {
				Location loc1 = player.getLocation();
				float grade = Math.abs(loc.getYaw() - loc1.getYaw()) + Math.abs(loc.getPitch() - loc1.getPitch());

				if (!(grade == 0)) {
					return;
				}
				if (block.getBlock().getType().isSolid() || !targetBlock.equals(event.getClickedBlock())) {
					WarnHacks.warnHacks(player, "GhostHand", 7, -1.0D, 36, "InvalidLocation", false);
				}
			}
		}, 2L);
	}

}
