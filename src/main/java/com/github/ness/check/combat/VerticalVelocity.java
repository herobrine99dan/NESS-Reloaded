package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class VerticalVelocity extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
	private double minVelocityPercentage = 95;
	private double buffer;

	public VerticalVelocity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		 this.minVelocityPercentage =
		 this.ness().getMainConfig().getCheckSection().verticalVelocity()
		 .minVelocityPercentage();
	}

	public interface Config {
		@DefaultDouble(95)
		 double minVelocityPercentage();
	}

	private List<Float> lastYDistances = new ArrayList<Float>();

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		MovementValues values = this.player().getMovementValues();
		double yDelta = values.getyDiff();
		NessPlayer nessPlayer = this.player();
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 500) {
			lastYDistances.add((float) yDelta); // We actually get the last 5 movements when the player gets velocity
			if (lastYDistances.size() > 7) {
				float max = Collections.max(lastYDistances); // We get the max value and we check how much it is similar
																// to the velocity
				float ySubtract = (float) (max / nessPlayer.getLastVelocity().getY());
				lastYDistances.clear();
				float percentage = Math.abs(ySubtract * 100);
				if (percentage < minVelocityPercentage && ++buffer > 1) {
					this.flag("percentage: " + percentage);
				} else if (buffer > 0) {
					buffer -= 0.5;
				}
			}
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 1000) {
			lastYDistances.clear(); // This is done to remove memory leaks
		}
	}

}