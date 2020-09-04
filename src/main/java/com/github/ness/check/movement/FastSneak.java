package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FastSneak extends AbstractCheck<PlayerMoveEvent> {

	public FastSneak(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.manager.getPlayer(p);
		float dist = (float) Math.abs(nessPlayer.getMovementValues().XZDiff); // Our XZ Distance
		if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1300) {
			dist -= Math.abs(nessPlayer.velocity.getX()) + Math.abs(nessPlayer.velocity.getZ());
		}
		double walkSpeed = p.getWalkSpeed() * 0.8;
		dist -= (dist / 100.0) * (Utility.getPotionEffectLevel(p, PotionEffectType.SPEED) * 20.0);
		walkSpeed += Math.abs(p.getVelocity().getY()) * 0.4;
		if (dist > walkSpeed && p.isSneaking() && !Utility.hasflybypass(p)) {
			if (Math.abs(nessPlayer.getMovementValues().xDiff) > walkSpeed
					|| Math.abs(nessPlayer.getMovementValues().zDiff) > walkSpeed) {
				nessPlayer.setViolation(new Violation("FastSneak", "Dist: " + (float) dist), event);
			}
		}
	}

}
