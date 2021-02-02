package com.github.ness.check.movement.fly;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

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
		if (e.getPlayer().isOnGround()) {
			airTicks = 0;
		} else {
			airTicks++;
		}
		if (values.getHelper().hasflybypass(nessPlayer) || p.getAllowFlight() || values.isAroundLiquids()
				|| Utility.hasVehicleNear(p) || values.isAroundWeb() || values.isAroundSlime()
				|| values.isAroundLadders()) {
			return;
		}
		nessPlayer.sendDevMessage("FlyGravity: " + 1);
		double yPredicted = (lastDeltaY - 0.08D) * 0.9800000190734863D;
		double yResult = Math.abs(deltaY - yPredicted);
		nessPlayer.sendDevMessage("FlyGravity: " + 2);
		if (airTicks > 3 && nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 3000 && Math.abs(yPredicted) > 0.05) {
			nessPlayer.sendDevMessage("FlyGravity: " + 3);
			if (Math.abs(yResult) > 0.001 && !isAtLeastFollowingGravity(deltaY, yPredicted)) {
				nessPlayer.sendDevMessage("NotCheats: " + (float) yResult + " Y: " + (float) deltaY + " PredictedY: "
						+ (float) yPredicted);
				if (++buffer > 0) {
					this.flagEvent(e, "yResult: " + yResult + " AirTicks: " + airTicks);
				}
			} else if (buffer > 0) {
				buffer -= 0.5;
			}
		}
		lastDeltaY = deltaY;
	}

	private boolean isAtLeastFollowingGravity(double yDelta, double yPredicted) {
		double nextYDelta = (yPredicted - 0.08) * 0.98f;
		double backYDelta = (yPredicted / 0.98f) + 0.08;
		double nextYResult = Math.abs(yDelta - nextYDelta);
		double backYResult = Math.abs(yDelta - backYDelta);
		if (Math.abs(nextYResult) > 0.001) {
			return false;
		}
		if (Math.abs(backYResult) > 0.001) {
			return false;
		}
		return true;
	}
}
