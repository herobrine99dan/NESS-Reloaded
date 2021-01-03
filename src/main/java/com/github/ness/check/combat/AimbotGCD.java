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
import com.github.ness.utility.Utility;

public class AimbotGCD extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	private List<Double> pitchDiff = new ArrayList<Double>();
	private double lastGCD = 0;
	private int lastSensitivity;

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
		if (pitchDiff.size() >= 10) {
			final double gcd = MathUtils.gcdRational(pitchDiff);
			if (lastGCD == 0.0) {
				lastGCD = gcd;
			}
			double result = Math.abs(gcd - lastGCD);
			final int sensitivity =  (int) Math.round(MathUtils.getSensitivity(gcd) * 200);
			player.sendDevMessage("GCD: " + (float) gcd + "Sensitivity: " + sensitivity);
			if (result < 0.007) {
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
