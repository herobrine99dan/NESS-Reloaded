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

	double maxInvalidVelocity;

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyInvalidClientGravity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.maxInvalidVelocity = this.ness().getMainConfig().getCheckSection().fly().maxGravity();
	}

	private double lastDeltaY;

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
		NessPlayer np = this.player();
		Player p = e.getPlayer();
		MovementValues values = np.getMovementValues();
		double y = values.getyDiff();
		if (Utility.hasflybypass(p) || p.getAllowFlight() || values.isAroundLiquids() || Utility.hasVehicleNear(p, 3)
				|| Utility.hasVehicleNear(p, 3) || values.isAroundWeb() || values.hasBlockNearHead() || np.isTeleported()) {
			return;
		}
		double yPredicted = Math.abs((lastDeltaY - 0.08D) * 0.9800000190734863D);
		if (np.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			yPredicted = np.getLastVelocity().getY();
		}
		double yResult = Math.abs(y - yPredicted);
		//TODO Needs better ground test
		if (yResult > 0.004D && !values.isGroundAround()) {
			np.sendDevMessage("Predicted: " + (float) yResult + " Y: " + (float) y);
		}
		lastDeltaY = y;
	}
}
