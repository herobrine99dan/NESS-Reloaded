package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class AntiKb extends AbstractCheck<EntityDamageByEntityEvent> {

	public AntiKb(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
	}

	/**
	 * Check if player move a bit
	 * 
	 * @param event
	 */
	public void Check(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			final Location from = p.getLocation();
			Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
				Location to = p.getLocation();
				if (to.distanceSquared(from) < 0.1 && !Utility.hasKbBypass(p)) {
					try {
						ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
								.getConfigurationSection("cancel");
						if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()),
								0) > cancelsec.getInt("vl", 10)) {
							event.setCancelled(true);
						}
					} catch (Exception ex) {
					}
					manager.getPlayer(p).setViolation(new Violation("AntiKb", ""));
				}
			}, 3L);
		}
	}
}
