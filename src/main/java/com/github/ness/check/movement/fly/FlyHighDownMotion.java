package com.github.ness.check.movement.fly;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class FlyHighDownMotion extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyHighDownMotion(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}
	
	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = player().getMovementValues();
		double yDiff = values.getyDiff();
		if (this.player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
			if (this.player().getLastVelocity().getY() < 0) {
				yDiff -= this.player().getLastVelocity().getY();
			}
		}
		if (yDiff < -3.40) {
			this.flagEvent(event, "Y: " + (float) yDiff);
		}
	}

}
