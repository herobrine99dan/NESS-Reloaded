package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.ReflectionUtility;
import com.github.ness.utility.Utility;

public class FlyHighJump extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private double flyYSum;

	public FlyHighJump(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}
	
	@Override
	protected boolean shouldDragDown() {
		return true;
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
				|| movementValues.AroundLadders || nessPlayer.isTeleported() || p.isInsideVehicle() || Utility.hasVehicleNear(p, 3) || nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 1000) {
			flyYSum = 0;
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500) {
			y -= Math.abs(nessPlayer.getLastVelocity().getY());
		}
		if (y > 0) {
			flyYSum += y;
			double max = 1.30;
			double jumpBoost = Utility.getPotionEffectLevel(p, PotionEffectType.JUMP);
			max += jumpBoost * (max / 2);
			if (flyYSum > max && p.getVelocity().getY() < 0) {
				flagEvent(e , " ySum: " + flyYSum);
	        	//if(player().setViolation(new Violation("Fly", "HighJump ySum: " + flyYSum))) e.setCancelled(true);
			}
		}
	}

}
