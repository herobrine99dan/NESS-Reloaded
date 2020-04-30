package com.github.ness.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.ness.NESS;
import com.github.ness.NESSPlayer;
import com.github.ness.Utility;
import com.github.ness.WarnHacks;

public class InventoryHack {
	public static void Check(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			if(Utility.hasflybypass(player)) {
				return;
			}
			if (player.isSprinting() || player.isSneaking() || player.isBlocking() || player.isSleeping()
					|| player.isConversing()) {
				WarnHacks.warnHacks(player, "InventoryMove", 10, 250, 9, "InvMove", false);
			} else {
				final Location from = player.getLocation();
				Bukkit.getScheduler().runTaskLater(NESS.main, new Runnable() {
					public void run() {
						Location to = player.getLocation();
						double distance = to.distanceSquared(from) - Math.abs(from.getY() - to.getBlockY());
						if (distance > 0.05) {
							WarnHacks.warnHacks(player, "InventoryMove", 10, -1.0D, 10, "InvMove", false);
							// MSG.tell(player, "Distance " + distance);
						}
					}
				}, 2L);
			}
		}
	}
	
	public static void Check2(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			NESSPlayer p = NESSPlayer.getInstance(player);
			p.SetClicks(p.getClicks()+1);
            if(p.getClicks()>4) {
           	 WarnHacks.warnHacks(player, "InventoryHacks", 10, -1.0D, 10, "FastClick", true);
           	 e.setCancelled(true);
            }
		}
	}
	
}
