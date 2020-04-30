package org.mswsplex.MSWS.NESS.checks.combat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class NoSwing {
	public static HashMap<UUID, Long> delay = new HashMap<UUID, Long>();

	public static void interactEvent(PlayerInteractEvent event) {
	}

	public static void damageEvent(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getDamager();
		if(delay.getOrDefault(p.getUniqueId(), (long) 1)> 1) {
			WarnHacks.warnHacks(p, "Killaura", 5, 600, 5, "NoSwing", false);
		}
		delay.put(p.getUniqueId(), System.currentTimeMillis());
	}

	public static void animationEvent(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			delay.put(event.getPlayer().getUniqueId(), (long) 1);
		}
	}

}
