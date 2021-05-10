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
	private double buffer = 0;

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
		if (movementValues.isOnGroundCollider()
				|| movementValues.getHelper().hasflybypass(nessPlayer) || movementValues.isAroundSlime()
				|| p.getAllowFlight() || movementValues.isAroundLiquids() || movementValues.isAroundLily()
				|| movementValues.isAroundSeaBlocks() || movementValues.isAroundSlabs()
				|| movementValues.isAroundStairs() || movementValues.isAroundLiquids()
				|| this.ness().getMaterialAccess().getMaterial(e.getTo().clone().add(0, -0.5, 0)).name()
						.contains("SCAFFOLD")
				|| this.ness().getMaterialAccess().getMaterial(e.getTo().clone().add(0, 0.5, 0)).name()
						.contains("SCAFFOLD")
				|| movementValues.isAroundKelp() || movementValues.isAroundSnow() || movementValues.isAroundLadders()
				|| nessPlayer.isTeleported() || movementValues.hasBlockNearHead() || Utility.hasVehicleNear(p)) {
			flyYSum = 0;
			return;
		}

		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500) {
			y -= Math.abs(nessPlayer.getLastVelocity().getY());
		}

		if (y > 0) {
			flyYSum += y;
			double max = 1.27;
			if (nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 1000) {
				max += 0.25;
			}
			double jumpBoost = Utility.getPotionEffectLevel(p, PotionEffectType.JUMP);
			max += jumpBoost * (max / 2);
			if (flyYSum > max) {
				if (++buffer > 1) {
					flagEvent(e, " ySum: " + flyYSum);
				}
				// if(player().setViolation(new Violation("Fly", "HighJump ySum: " + flyYSum)))
				// e.setCancelled(true);
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
	}

}
