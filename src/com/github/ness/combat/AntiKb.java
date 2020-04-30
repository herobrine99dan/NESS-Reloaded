package com.github.ness.combat;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.MSG;
import com.github.ness.NESS;
import com.github.ness.Utility;
import com.github.ness.WarnHacks;

public class AntiKb {
	static HashMap<Player, Entity> lastHitBy = new HashMap<Player, Entity>();

	public static void Check(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.PLAYER && !event.isCancelled()) {
			Player player = (Player) event.getEntity();
			final Player p = player;
			lastHitBy.put(player, event.getDamager());
			Location hitAt = event.getEntity().getLocation();
			Bukkit.getScheduler().scheduleSyncDelayedTask(NESS.main, () -> {
				Location hitTo = event.getEntity().getLocation();
				double dist = hitAt.distanceSquared(hitTo);
				if (dist < 0.15D && !Utility.hasKbBypass(player)) {
					WarnHacks.warnHacks(p, "AntiKB", (int) 5, 500.0D, 1,
							"Low Velocity", false);
					if (NESS.main.devMode) {
						MSG.tell(event.getEntity(), "AntiKnockback Detected! &9Dev> &7KB Dist:" + dist);
					}
				}

			}, 4L);
		}
	}
}
