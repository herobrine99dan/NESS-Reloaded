package com.github.ness.check.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class FastSneak extends AbstractCheck<PlayerMoveEvent> {

	public FastSneak(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		float dist = (float) Math.abs(np.getMovementValues().XZDiff); // Our XZ Distance
		double walkSpeed = p.getWalkSpeed() * 0.8;
		dist -= (dist / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		walkSpeed += Math.abs(p.getVelocity().getY()) * 0.4;
		if (dist > walkSpeed && p.isSneaking()) {
			if(Math.abs(np.getMovementValues().xDiff) > walkSpeed || Math.abs(np.getMovementValues().zDiff) > walkSpeed) {
				np.setViolation(new Violation("FastSneak", "Dist: " + (float) dist), event);
			}
		}
	}

}
