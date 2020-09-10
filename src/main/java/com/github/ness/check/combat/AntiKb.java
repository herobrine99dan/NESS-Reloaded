package com.github.ness.check.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class AntiKb extends AbstractCheck<EntityDamageByEntityEvent> {

	public AntiKb(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
	}

	/**
	 * Check if player move a bit
	 * 
	 * @param event
	 */
	public void Check(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			final Location from = player.getLocation();
			if (Utility.isClimbableBlock(from.getBlock()) || Utility.specificBlockNear(from.clone(), "web")
					|| Utility.hasKbBypass(player)) {
				return;
			}
			Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
				Location to = player.getLocation();
				if (to.distanceSquared(from) < .4 && !player.getLocation().add(0, 2, 0).getBlock().getType().isSolid()
						&& !player.getLocation().getBlock().getType().isSolid()) {
					manager.getPlayer(player).setViolation(new Violation("AntiKb", ""), null);
				}
			}, 5L);
		}
	}
}
