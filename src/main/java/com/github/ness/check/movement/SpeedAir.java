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
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class SpeedAir extends AbstractCheck<PlayerMoveEvent> {

	public SpeedAir(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.getNessPlayer(p);
		Location to = event.getTo().clone();
		double xDiff = nessPlayer.getMovementValues().xDiff;
		double zDiff = nessPlayer.getMovementValues().zDiff;
		if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1500) {
			xDiff -= nessPlayer.velocity.getX();
			zDiff -= nessPlayer.velocity.getZ();
		}
		final double total = Math.abs(xDiff) + Math.abs(zDiff);
		if (nessPlayer.airTicks > 4 && (Math.abs(xDiff) > getBaseSpeed(p) || Math.abs(zDiff) > getBaseSpeed(p))) {
			nessPlayer.setViolation(new Violation("Speed", "AirCheck Dist: " + total), event);
		}
	}

	public static float getBaseSpeed(Player player) {
		float max = 0.36f + (Utility.getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f)
				+ ((player.getWalkSpeed() - 0.2f) * 1.6f);
		if (Utility.getMaterialName(player.getLocation().clone().add(0, -0.6, 0)).contains("ice")
				|| Utility.specificBlockNear(player.getLocation().clone(), "ice")) {
			max *= 1.4;
		}
		return max;
	}

}
