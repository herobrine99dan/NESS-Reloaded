package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class NoWeb extends AbstractCheck<PlayerMoveEvent> {

	public NoWeb(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		float dist = (float) np.getMovementValues().XZDiff; // Our XZ Distance
		final double walkSpeed = p.getWalkSpeed() * 0.85;
		dist -= (dist / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		if (dist > walkSpeed && Utility.getMaterialName(event.getTo()).contains("web")
				&& Utility.getMaterialName(event.getFrom()).contains("web") && Utility.hasflybypass(p)) {
			np.setViolation(new Violation("NoWeb", "Dist: " + (float) dist), event);
		}
	}

}
