package com.github.ness.check;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.Violation;
import com.github.ness.utility.Utility;

public class Criticals extends AbstractCheck<EntityDamageByEntityEvent>{

	public Criticals(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
       Check(e);
	}
	/**
	 * Check for Criticals Pattern
	 * @param event
	 */
	public void Check(EntityDamageByEntityEvent event) {
		if (!(event.getDamager().getType() == EntityType.PLAYER)) {
			return;
		}
		Player player = (Player) event.getDamager();
		if (Utility.hasflybypass(player)) {
			return;
		}
			if (player.getLocation().getY() % 1.0D == 0.0D || player.getLocation().getY() % 0.5 == 0) {
				if (!player.isInsideVehicle()
						&& !player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
					manager.getPlayer((Player) event.getEntity()).setViolation(new Violation("Criticals"));
				}
			}
	}

}
