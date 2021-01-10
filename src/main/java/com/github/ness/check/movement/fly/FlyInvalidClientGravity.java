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

public class FlyInvalidClientGravity extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyInvalidClientGravity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	private double lastDeltaY;
	private int airTicks;
	private double buffer;

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Check(e);
	}

	/**
	 * Check for Invalid Gravity
	 *
	 * @param e
	 */
	public void Check(PlayerMoveEvent e) {
		NessPlayer nessPlayer = this.player();
		Player p = e.getPlayer();
		MovementValues values = nessPlayer.getMovementValues();
		double deltaY = values.getyDiff();
		if (values.getHelper().isMathematicallyOnGround(values.getTo().getY())) {
			airTicks = 0;
		} else {
			airTicks++;
		}
		if (values.getHelper().hasflybypass(nessPlayer) || p.getAllowFlight() || values.isAroundLiquids() || nessPlayer.getMovementValues().getHelper().isVehicleNear()
				|| values.isAroundWeb() || values.isAroundLadders() || values.hasBlockNearHead() || values.isAroundNonOccludingBlocks()) {
			return;
		}
		double yPredicted = (lastDeltaY - 0.08D) * 0.9800000190734863D;
		double yResult = Math.abs(deltaY - yPredicted);
		if (yResult > 0.005 && Math.abs(yPredicted) > 0.005 && airTicks > 15
				&& nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 3000) {
			nessPlayer.sendDevMessage("NotCheats: " + (float) yResult + " Y: " + (float) deltaY + " PredictedY: " + (float) yPredicted + " velocity:" + (float) p.getVelocity().getY());
			if (++buffer > 4) {
				this.flagEvent(e, "yResult: " + yResult + " AirTicks: " + airTicks);
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		lastDeltaY = deltaY;
	}
}
