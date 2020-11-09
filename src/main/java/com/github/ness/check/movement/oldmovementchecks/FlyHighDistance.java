package com.github.ness.check.movement.oldmovementchecks;

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

public class FlyHighDistance extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	int preVL;

	public FlyHighDistance(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		preVL = 0;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = player().getMovementValues();
		Player player = e.getPlayer();
		double dist = values.XZDiff;
		if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
				|| player().isTeleported()) {
			return;
		}
		if (player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 1600) {
			dist -= Math.abs(player().getLastVelocity().getX()) + Math.abs(player().getLastVelocity().getZ());
		}
		if (!values.isGroundAround() && dist > 0.35 && values.yDiff == 0.0
				&& this.player().getTimeSinceLastWasOnIce() >= 1000) {
			if (preVL++ > 1) {
				flagEvent(e);
			}
		} else if (preVL > 0) {
			preVL--;
		}
	}

}
