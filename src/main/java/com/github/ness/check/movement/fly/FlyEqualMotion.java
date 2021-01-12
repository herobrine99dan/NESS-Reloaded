package com.github.ness.check.movement.fly;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

public class FlyEqualMotion extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyEqualMotion(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	private double lastYDiff;
	private double buffer;

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = player().getMovementValues();
		double yDiff = values.getyDiff();
		if (this.player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000 || values.isAroundSnow()
				|| values.isAroundCarpet() || values.isAroundLadders() || values.isAroundSlabs()
				|| values.isAroundStairs() || values.getHelper().hasflybypass(player()) || values.isAroundWeb()) {
			return;
		}
		if(yDiff == 0.0 && buffer > 0) {
			buffer -= 0.5;
			return;
		}
		double result = yDiff - lastYDiff;
		if (Math.abs(result) < 1e-6) {
			if (++buffer > 2) {
				this.flagEvent(event);
			}
		} else if (buffer > 0) {
			buffer -= 0.5;
		}
		lastYDiff = yDiff;
	}

}
