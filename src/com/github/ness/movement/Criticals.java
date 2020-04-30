package com.github.ness.movement;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.Utility;
import com.github.ness.WarnHacks;

public class Criticals {

	public static void Check(EntityDamageByEntityEvent event) {
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
					WarnHacks.warnHacks(player, "Criticals", 40, -1.0D, 15, "InvalidLocation", false);
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
