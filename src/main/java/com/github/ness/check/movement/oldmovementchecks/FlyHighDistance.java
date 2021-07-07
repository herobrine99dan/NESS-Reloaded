package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.Material;
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
		double dist = values.getXZDiff();
		if (values.getHelper().hasflybypass(player()) || player.getAllowFlight() || Utility.hasVehicleNear(player)
				|| player().isTeleported()) {
			return;
		}
		if (player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 1600) {
			dist -= Math.abs(player().getLastVelocity().getX()) + Math.abs(player().getLastVelocity().getZ());
		}
		Material below = this.ness().getMaterialAccess().getMaterial(e.getTo().clone().subtract(0, 1, 0));
		if (player.getLocation().getY() % .5 != 0.0 && !player.isFlying() && !values.isGroundAround()
				&& below.isOccluding() && !values.getHelper().isOnGround(e.getTo(), "WEB", "LILY", "FENCE")
				&& !values.getHelper().isOnGround(e.getTo())) {
			if (preVL++ > 2) {
				flagEvent(e);
			}
		} else if (preVL > 0) {
			preVL--;
		}
	}

}
