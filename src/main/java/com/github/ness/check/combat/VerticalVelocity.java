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
	private int velocityTicks = 0;
	private float yVelocity;

	public VerticalVelocity(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.minVelocityPercentage = this.ness().getMainConfig().getCheckSection().verticalVelocity()
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
		float yDelta = (float) values.getyDiff();
		NessPlayer nessPlayer = this.player();
		if (getMaterialName(values.getTo()).contains("WEB")) {
			return;
		}
		if (nessPlayer.hasAlreadyVerticalVelocityUsedVelocity()) {
			nessPlayer.setAlreadyVerticalVelocityUsedVelocity(false);
			yVelocity = (float) nessPlayer.getLastVelocity().getY();
			velocityTicks = 5;
		}
		if(yVelocity < 0) {
			velocityTicks = 0;
			return;
		}
		if (velocityTicks > 0) {
			lastYDistances.add(yDelta); // We actually get the last 5 movements of when the player gets velocity
			if (lastYDistances.size() > 4) {
				float max = Collections.max(lastYDistances); // We get the max value and we check how much it is similar
																// to the velocity
				float ySubtract = max / yVelocity;
				lastYDistances.clear();
				float percentage = Math.abs(roundNumber(ySubtract * 100, 100));
				if (percentage < minVelocityPercentage) {
					this.flag("percent: " + percentage + " velocity: " + roundNumber(yVelocity, 1000));
				} else if (buffer > 0) {
					buffer -= 0.5;
				}
			}
			velocityTicks--;
		}
	}
	
	private float roundNumber(float n, float places) {
		return Math.round(n * places) / places;
	}

}