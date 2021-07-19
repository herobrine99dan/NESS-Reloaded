package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.MultipleListeningCheck;
import com.github.ness.check.MultipleListeningCheckFactory;
import com.github.ness.data.MovementValues;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class VerticalVelocity extends MultipleListeningCheck {

	public static final CheckInfo checkInfo = CheckInfos.forMultipleEventListener(PlayerMoveEvent.class,
			PlayerVelocityEvent.class);
	private final double minVelocityPercentage;
	private double buffer;
	private int velocityTicks = 0;
	private float yVelocity;
	private boolean velocityAlreadyUsed = false;

	public VerticalVelocity(MultipleListeningCheckFactory<?> factory, NessPlayer player) {
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
	protected void checkEvent(Event event) {
			Player player = ((PlayerEvent) event).getPlayer(); //It must extends at least PlayerEvent, we declared this before!
			if (player().isNot(player))
				return;
			if(event instanceof PlayerVelocityEvent) onVelocity((PlayerVelocityEvent) event);
			if(event instanceof PlayerMoveEvent) onMove((PlayerMoveEvent) event);
	}

	private void onVelocity(PlayerVelocityEvent e) {
		yVelocity = (float) e.getVelocity().getY();
		velocityAlreadyUsed = true;
	}

	private void onMove(PlayerMoveEvent e) {
		MovementValues values = this.player().getMovementValues();
		float yDelta = (float) values.getyDiff();
		if (getMaterialName(values.getTo()).contains("WEB")) {
			return;
		}
		if (velocityAlreadyUsed) {
			velocityAlreadyUsed = false;
			velocityTicks = 5;
		}
		if (yVelocity < 0) {
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