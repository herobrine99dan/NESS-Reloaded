package org.mswsplex.MSWS.NESS.checks.killaura;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.NESSPlayer;
import org.mswsplex.MSWS.NESS.Utilities;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class PatternKillaura {
	static HashMap<UUID, Long> lastAttack = new HashMap<UUID, Long>();
	static HashMap<UUID, LivingEntity> lastHit = new HashMap<UUID, LivingEntity>();
	static HashMap<UUID, Float> lastRange = new HashMap<UUID, Float>();

	public static void Check(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			Entity entity = event.getEntity();

			if (entity instanceof LivingEntity) {
				lastAttack.put(p.getUniqueId(), System.currentTimeMillis());
				lastHit.put(p.getUniqueId(), (LivingEntity) entity);
			}
		}
	}

	public static void Check1(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		if ((System.currentTimeMillis() - lastAttack.getOrDefault(uuid, System.currentTimeMillis()) > 350L)
				|| lastHit.get(uuid) == null) {
			return;
		}
		NESSPlayer np = NESSPlayer.getInstance(p);
		List<Float> patterns = np.patterns;
		float offset = Utilities
				.yawTo180F((float) Utilities.getOffsetFromEntity(event.getPlayer(), lastHit.get(uuid))[0]);

		if (patterns.size() >= 23) {
			// TODO Check

			Collections.sort(patterns);

			float range = Math.abs(patterns.get(patterns.size() - 1) - patterns.get(0));

			if (Math.abs(range - lastRange.getOrDefault(uuid, 0.0f)) < 4) {
				WarnHacks.warnHacks(p, "Killaura", 3, -1.0D, 1, "ClickPattern", false);
				if (NESS.main.devMode) {
					p.sendMessage("KillauraPattern: " + Math.abs(range - lastRange.getOrDefault(uuid, 0.0f)));
				}
			}
			// event.getPlayer().sendMessage("Range: " + range);

			lastRange.put(uuid, range);
			patterns.clear();
		} else {
			patterns.add(offset);
		}

	}

}
