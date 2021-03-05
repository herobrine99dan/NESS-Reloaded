package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FlyEqualDistance extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyEqualDistance(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		MovementValues values = player().getMovementValues();
		if (values.getHelper().hasflybypass(player()) || player().isTeleported()) {
			return;
		}
		double dist = e.getTo().distance(e.getFrom());
		if (dist == 0.0D && !values.isGroundAround() && !values.isAroundWeb() && !Utility.hasVehicleNear(e.getPlayer()) && !Utility.hasLivingEntityNear(e.getPlayer())
				&& !values.isAroundSlime() && !values.isAroundLadders() && !values.isAroundCactus()
				&& this.player().milliSecondTimeDifference(PlayerAction.ATTACK) > 500)
			this.flagEvent(e);
	}

}
