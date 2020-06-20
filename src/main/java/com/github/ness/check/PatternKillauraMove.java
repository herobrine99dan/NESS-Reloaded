package com.github.ness.check;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utilities;

public class PatternKillauraMove extends AbstractCheck<PlayerMoveEvent> {
	
	public PatternKillauraMove(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
       Check(e);
	}
	
	public void Check(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();
		if ((System.currentTimeMillis() - PatternKillauraAttack.lastAttack.getOrDefault(uuid, System.currentTimeMillis()) > 350L)
				|| PatternKillauraAttack.lastHit.get(uuid) == null) {
			return;
		}
		NessPlayer np = manager.getPlayer(p);
		List<Float> patterns = np.getPatterns();
		float offset = Utilities
				.yawTo180F((float) Utilities.getOffsetFromEntity(event.getPlayer(), PatternKillauraAttack.lastHit.get(uuid))[0]);

		if (patterns.size() >= 23) {
			// TODO Check

			Collections.sort(patterns);

			float range = Math.abs(patterns.get(patterns.size() - 1) - patterns.get(0));

			if (Math.abs(range - PatternKillauraAttack.lastRange.getOrDefault(uuid, 0.0f)) < 4) {
				if(manager.getPlayer(event.getPlayer()).shouldCancel(event, "Killaura")) {
					event.setCancelled(true);
				}
				np.setViolation(new Violation("Killaura","Result: "+Math.abs(range - PatternKillauraAttack.lastRange.getOrDefault(uuid, 0.0f))));
			}
			// event.getPlayer().sendMessage("Range: " + range);

			PatternKillauraAttack.lastRange.put(uuid, range);
			patterns.clear();
		} else {
			patterns.add(offset);
		}
	}

}
