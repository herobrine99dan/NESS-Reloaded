package com.github.ness.movement;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import com.github.ness.NESSPlayer;
import com.github.ness.Utility;
import com.github.ness.WarnHacks;

public class NoSlowDown {
	private static double maxdist = 0.2;

	public static void ShootBowCheck(EntityShootBowEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			Player o = (Player) e.getEntity();
			if(Utility.hasflybypass(o)) {
				return;
			}
            NESSPlayer p = NESSPlayer.getInstance(o);
			double distance = p.getDistance();
			/*
			 * if (o.isSprinting() || failed==1) { e.setCancelled(true);
			 * checkfailed(o.getName()); }
			 */
			if (distance > maxdist||o.isSprinting()) {
				e.setCancelled(true);
				punish(o);
			}
		}
	}
	
	private static void punish(Player p) {
		WarnHacks.warnHacks(p, "NoSlowDown", 10, 500, 22, "HighDistance", false);
	}

	public static void FoodCheck(PlayerItemConsumeEvent e) {
		if(Utility.hasflybypass(e.getPlayer())) {
			return;
		}
		NESSPlayer p = NESSPlayer.getInstance(e.getPlayer());
		double distance = p.getDistance();
		if (distance > maxdist||e.getPlayer().isSprinting()) {
			e.setCancelled(true);
			punish(e.getPlayer());
		}
	}
}
