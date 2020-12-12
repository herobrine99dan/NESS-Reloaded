package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
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
		double y = movementValues.getyDiff();
		if (Utility.isMathematicallyOnGround(e.getTo().getY()) || movementValues.isOnGroundCollider()
				|| Utility.hasflybypass(p) || movementValues.isAroundSlime() || p.getAllowFlight()
				|| Utility.isInWater(p) || movementValues.isAroundLily()
				|| Utility.specificBlockNear(e.getTo().clone(), "SEA") || movementValues.isAroundSlabs()
				|| movementValues.isAroundStairs() || movementValues.isAroundLiquids()
				|| this.ness().getMaterialAccess().getMaterial(e.getTo().clone().add(0, -0.5, 0)).name()
						.contains("SCAFFOLD")
				|| this.ness().getMaterialAccess().getMaterial(e.getTo().clone().add(0, 0.5, 0)).name()
						.contains("SCAFFOLD")
				|| movementValues.isAroundSnow() || movementValues.isAroundLadders() || nessPlayer.isTeleported()
				|| movementValues.hasBlockNearHead() || Utility.hasVehicleNear(p, 3)
				|| nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 1000) {
			flyYSum = 0;
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500) {
			y -= Math.abs(nessPlayer.getLastVelocity().getY());
		}

		if (y > 0) {
			flyYSum += y;
			double max = 1.45;
			double jumpBoost = Utility.getPotionEffectLevel(p, PotionEffectType.JUMP);
			max += jumpBoost * (max / 2);
			if (flyYSum > max && p.getVelocity().getY() < 0) {
				flagEvent(e, " ySum: " + flyYSum);
				// if(player().setViolation(new Violation("Fly", "HighJump ySum: " + flyYSum)))
				// e.setCancelled(true);
			}
		}
	}

}
