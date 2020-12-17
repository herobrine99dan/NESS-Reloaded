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
		double y = values.getyDiff();
		if (values.isOnGroundCollider()) {
			airTicks = 0;
		} else {
			airTicks++;
		}
		if (Utility.hasflybypass(p) || p.getAllowFlight() || values.isAroundLiquids() || Utility.hasVehicleNear(p, 3)
				|| values.isAroundWeb() || values.isAroundLadders() || values.hasBlockNearHead()) {
			return;
		}
		double yPredicted = (lastDeltaY - 0.08D) * 0.98D;
		double yResult = Math.abs(y - yPredicted);
		if (yResult > 0.002 && Math.abs(yPredicted) > 0.004 && airTicks > 5
				&& nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 3000) {
			nessPlayer.sendDevMessage("NotCheats: " + (float) yResult + " Y: " + (float) y + " AirTicks: " + airTicks
					+ " Buffer: " + buffer);
			buffer += 2;
			if (buffer > 3) {
				this.flagEvent(e, "yResult: " + yResult + " AirTicks: " + airTicks);
				// np.sendDevMessage("Cheats: " + (float) yResult + " Y: " + (float) y + "
				// AirTicks: " + airTicks);
			}
		} else if (buffer > 0) {
			buffer -= .25;
		}
		lastDeltaY = y;
	}
}
