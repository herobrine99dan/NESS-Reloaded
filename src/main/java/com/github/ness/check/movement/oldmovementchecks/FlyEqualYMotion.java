package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.utility.Utility;

public class FlyEqualYMotion extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyEqualYMotion(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = player().getMovementValues();
		Player player = e.getPlayer();
		if (values.getHelper().hasflybypass(player()) || player.getAllowFlight() || Utility.hasVehicleNear(player)
				|| player().isTeleported()) {
			return;
		}
		if (Math.abs(values.getyDiff()) < 0.0001) {
			if (!values.isGroundAround()) {
				if (values.getXZDiff() > 0.35D && values.isAroundIce()) {
					this.flagEvent(e);
				}
			}
		}
	}
}
