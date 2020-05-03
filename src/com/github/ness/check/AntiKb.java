package com.github.ness.check;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mswsplex.MSWS.NESS.NESS;
import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.Utility;
import com.github.ness.Violation;

public class AntiKb extends AbstractCheck<EntityDamageByEntityEvent> {
	
	public AntiKb(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
       Check(e);
	}
	
	 HashMap<Player, Entity> lastHitBy = new HashMap<Player, Entity>();

	public void Check(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.PLAYER && !event.isCancelled()) {
			Player player = (Player) event.getEntity();
			final Player p = player;
			lastHitBy.put(player, event.getDamager());
			Location hitAt = event.getEntity().getLocation();
			Bukkit.getScheduler().scheduleSyncDelayedTask(manager.getNess(), () -> {
				Location hitTo = event.getEntity().getLocation();
				double dist = hitAt.distanceSquared(hitTo);
				if (dist < 0.15D && !Utility.hasKbBypass(player)) {
					manager.getPlayer((Player) event.getEntity()).setViolation(new Violation("AntiKb"));
					if (NESSAnticheat.main.devMode) {
						p.sendMessage("AntiKnockback Detected! &9Dev> &7KB Dist:" + dist);
					}
				}

			}, 4L);
		}
	}
}
