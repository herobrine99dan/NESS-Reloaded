package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.ReflectionUtility;
import com.github.ness.utility.Utility;

public class FlyHighJump extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo.eventOnly(PlayerMoveEvent.class);

	private double flyYSum;

	public FlyHighJump(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		NessPlayer nessPlayer = this.player();
		Player p = e.getPlayer();
		final MovementValues movementValues = nessPlayer.getMovementValues();
		double y = movementValues.yDiff;
		if (Utility.isMathematicallyOnGround(e.getTo().getY()) || Utility.hasflybypass(p) || movementValues.AroundSlime
				|| p.getAllowFlight() || Utility.isInWater(p) || movementValues.AroundLily
				|| Utility.specificBlockNear(e.getTo().clone(), "sea") || movementValues.AroundSlabs
				|| movementValues.AroundStairs || movementValues.AroundLiquids
				|| ReflectionUtility.getBlockName(p, ImmutableLoc.of(e.getTo().clone().add(0, -0.5, 0)))
						.contains("scaffolding")
				|| ReflectionUtility.getBlockName(p, ImmutableLoc.of(e.getTo().clone().add(0, 0.5, 0)))
						.contains("scaffolding")
				|| movementValues.AroundSnow
				|| movementValues.AroundLadders || nessPlayer.isTeleported()) {
			flyYSum = 0;
			return;
		}
		if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1500) {
			y -= Math.abs(nessPlayer.velocity.getY());
		}
		if (y > 0) {
			flyYSum += y;
			double max = 1.30;
			double jumpBoost = Utility.getPotionEffectLevel(p, PotionEffectType.JUMP);
			max += jumpBoost * (max / 2);
			if (flyYSum > max && p.getVelocity().getY() < 0) {
	        	if(player().setViolation(new Violation("Fly", "HighJump ySum: " + flyYSum))) e.setCancelled(true);
			}
		}
	}

}
