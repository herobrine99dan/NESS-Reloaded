package com.github.ness.check.combat;

import java.util.concurrent.TimeUnit;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.GCDUtils;

public class Aimbot extends AbstractCheck<ReceivedPacketEvent> {

	public Aimbot(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(ReceivedPacketEvent.class, 1, TimeUnit.SECONDS));
	}

	@Override
	protected void checkAsyncPeriodic(NessPlayer player) {
		player.AimbotPatternCounter = 0;
	}

	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		if (!e.getPacket().getName().toLowerCase().contains("look")) {
			return;
		}
		Check(e);
		// Check1(e);
		Check2(e);
		Check3(e);
	}

	public void Check(ReceivedPacketEvent event) {
		// float yaw = to.getYaw() - from.getYaw();
		NessPlayer player = event.getNessPlayer();
		float pitch = (float) Math.abs(player.getMovementValues().pitchDiff);
		if (Math.abs(pitch) >= 10 || Math.abs(pitch) < 0.05) {
			return;
		}
		if (pitch == 0.0) {
			return;
		}
		if (Math.abs(player.getMovementValues().getTo().getPitch()) == 90) {
			return;
		}
		player.pitchDiff.add(pitch);
		if (player.pitchDiff.size() >= 20) {
			final float gcd = GCDUtils.gcdRational(player.pitchDiff);
			if (player.lastGCD == 0.0) {
				player.lastGCD = gcd;
			}
			double result = Math.abs(gcd - player.lastGCD);
			if (result < 0.01) {
				final double sensitivity = GCDUtils.getSensitivity(gcd);
				if (player.isDevMode()) {
					player.getPlayer().sendMessage("Setting Sensitivity to: " + sensitivity);
				}
				player.sensitivity = sensitivity;
			}
			if (result > 0.001 || gcd < 0.0001) {
				// TODO Trying to fix Cinematic Mode
				player.setViolation(new Violation("Aimbot", "GCDCheck" + " GCD: " + (float) gcd), null);
			}
			// formatter.format(result));
			player.pitchDiff.clear();
			player.lastGCD = gcd;
		}
	}

	public void Check1(ReceivedPacketEvent e) {
		NessPlayer np = e.getNessPlayer();
		if (np.sensitivity == 0) {
			return;
		}
		if (np.getMovementValues().yawDiff < 1) {
			return;
		}
		double firstvar = np.sensitivity * 0.6F + 0.2F;
		float secondvar = (float) (Math.pow(firstvar, 3f) * 8.0F);
		double yawResult = np.getMovementValues().yawDiff - np.lastYaw;
		float thirdvar = (float) yawResult / (secondvar * 0.15F);
		float x = (float) (thirdvar - Math.floor(thirdvar));
		// TODO Fixing Smooth Camera
		if (x > 0.1 && x < 0.95) {
			np.setViolation(new Violation("Aimbot", "ImpossibleRotations: " + x), null);
		}
		np.lastYaw = (float) np.getMovementValues().yawDiff;
	}

	/**
	 * Check for some Aimbot Pattern
	 */
	public boolean Check2(ReceivedPacketEvent e) {
		NessPlayer player = e.getNessPlayer();
		float yawChange = (float) Math.abs(player.getMovementValues().yawDiff);
		float pitchChange = (float) Math.abs(player.getMovementValues().pitchDiff);
		if (yawChange >= 1.0f && yawChange % 0.1f == 0.0f) {
			player.setViolation(new Violation("Aimbot", "PerfectAura"), e);
			return true;
		} else if (pitchChange >= 1.0f && pitchChange % 0.1f == 0.0f) {
			player.setViolation(new Violation("Aimbot", "PerfectAura1"), e);
			return true;
		}
		return false;
	}

	public void Check3(ReceivedPacketEvent e) {
		NessPlayer np = e.getNessPlayer();
		final double yaw = Math.abs(np.getMovementValues().yawDiff);
		if ((Math.round(Math.abs(yaw)) == Math.abs(yaw) && yaw < 340 && yaw > 0)) {
			np.AimbotPatternCounter = np.AimbotPatternCounter + 1;
			if (np.AimbotPatternCounter > 3) {
				np.setViolation(new Violation("Aimbot", "Pattern3"), e);
				np.AimbotPatternCounter = 0;
			}
		}
	}
}