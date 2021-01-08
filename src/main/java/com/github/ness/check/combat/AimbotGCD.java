package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.MathUtils;

public class AimbotGCD extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private List<Double> pitchDiff = new ArrayList<Double>();
	private double lastGCD = 0;
	private int lastSensitivity;
	private double buffer;

	public AimbotGCD(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.pitchDiff = new ArrayList<Double>();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		NessPlayer player = player();
		double pitchDelta = Math.abs(player.getMovementValues().getPitchDiff());
		if (Math.abs(pitchDelta) >= 10 || Math.abs(pitchDelta) < 0.05 || pitchDelta == 0.0 || player.isTeleported()
				|| player.isHasSetback() || Math.abs(player.getMovementValues().getTo().getPitch()) == 90) {
			return;
		}
		pitchDiff.add(pitchDelta);
		if (pitchDiff.size() >= 15) {
			final float gcd = (float) MathUtils.gcdRational(pitchDiff);
			if (lastGCD == 0.0) {
				lastGCD = gcd;
			}
			double result = Math.abs(gcd - lastGCD);
			final int sensitivity = (int) Math.round(MathUtils.getSensitivity(gcd) * 200);
			player.sendDevMessage("GCD: " + gcd + " Sensitivity: " + sensitivity);
			if (result > 0.001 || gcd < 0.00001) {
				if (++buffer > 1) {
					this.flag("sensitivity: " + sensitivity);
				}
			} else if(buffer > 0) {
				buffer -= 0.5;
			}
			if (result < 0.01) {
				if (Math.abs(sensitivity - lastSensitivity) == 0) {
					player.setSensitivity(sensitivity);
				}
			}
			lastSensitivity = sensitivity;
			pitchDiff.clear();
			lastGCD = gcd;
		}
	}

}
