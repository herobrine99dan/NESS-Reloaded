package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class FastLadder extends AbstractCheck<PlayerMoveEvent> {
	
	double maxDist;

	public FastLadder(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		this.maxDist = this.manager.getNess().getNessConfig().getCheck(this.getClass())
				.getDouble("maxdist", 0.201D);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NESSPlayer np = this.manager.getPlayer(p);
		if (Utility.isClimbableBlock(p.getLocation().getBlock()) && !p.hasPotionEffect(PotionEffectType.JUMP)
				&& !Utility.hasflybypass(p) && !this.manager.getPlayer(p).isTeleported()) {
			double distance = np.getMovementValues().yDiff;
			if (distance > 0.155D && p.getVelocity().getY() < 0) {
				np.setViolation(new Violation("FastLadder", "Dist: " + (float) distance), event);
			}
		}
	}
}
