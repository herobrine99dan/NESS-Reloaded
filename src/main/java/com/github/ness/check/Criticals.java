package com.github.ness.check;

import org.bukkit.Material;
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
	
	public void Check(EntityDamageByEntityEvent event) {
		if (!(event.getDamager().getType() == EntityType.PLAYER)) {
			return;
		}
		Player player = (Player) event.getDamager();
		if (Utility.hasflybypass(player)) {
			return;
		}
		if (isCritical(player)) {
			if (player.getLocation().getY() % 1.0D == 0.0D || player.getLocation().getY() % 0.5 == 0) {
				if (player.getFallDistance() < 0.06251 && !player.isInsideVehicle()
						&& !player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
					manager.getPlayer((Player) event.getEntity()).setViolation(new Violation("Criticals"));
				}
			}
		}
	}

	private static boolean isCritical(Player player) {
		return player.getFallDistance() > 0.0f && !Utility.checkGround(player.getLocation().getY()) && !player.isInsideVehicle()
				&& !player.hasPotionEffect(PotionEffectType.BLINDNESS)
				&& player.getEyeLocation().getBlock().getType() != Material.LADDER;
	}

}
