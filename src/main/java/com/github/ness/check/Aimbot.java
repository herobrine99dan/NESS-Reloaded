package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.GCDUtils;

public class Aimbot extends AbstractCheck<ReceivedPacketEvent> {

	public Aimbot(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(ReceivedPacketEvent.class, 1, TimeUnit.SECONDS));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.AimbotPatternCounter = 0;
	}
	
	@Override
	void checkEvent(ReceivedPacketEvent e) {
		Check(e);
		Check1(e);
		Check3(e);
		Check4(e);
	}

	/**
	 * All changes in pitch should be divisible by a constant. That constant is
	 * determined by your mouse sensitivity in game. By calculating the gcd and
	 * finding that constant, you can detect changes in pitch variation.
	 * 
	 */
	public void Check(ReceivedPacketEvent event) {
		// float yaw = to.getYaw() - from.getYaw();
		NessPlayer player = event.getNessPlayer();
		double pitch = player.getMovementValues().pitchDiff;
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
			final double gcd = GCDUtils.gcdRational(player.pitchDiff);
			if (player.lastGCD == 0.0) {
				player.lastGCD = gcd;
			}
			double result = Math.abs(gcd - player.lastGCD);
			//NumberFormat formatter = new DecimalFormat("0.00000000000");
			//int sensitivityinteger = adaptSensitivity(sensitivity, gcd);
			//p.sendMessage("GCD: " + gcd + " Sensitivity: " + sensitivityinteger);
			if (result > 0.001 || gcd < 0.0001) {
				player.setViolation(new Violation("Aimbot", "GCDCheck" + "GCD: " + (float) gcd));
			}
			// formatter.format(result));
			player.pitchDiff.clear();
			player.lastGCD = gcd;
		}
	}

	/**
	 * Check for some Aimbot Pattern
	 */
	public boolean Check1(ReceivedPacketEvent e) {
		NessPlayer player = e.getNessPlayer();
		float yawChange = (float) Math.abs(player.getMovementValues().yawDiff);
		float pitchChange = (float) Math.abs(player.getMovementValues().pitchDiff);
		if (yawChange >= 1.0f && yawChange % 0.1f == 0.0f) {
			if (player.shouldCancel(e, this.getClass().getSimpleName())) {
				e.setCancelled(true);
			}
			player.setViolation(new Violation("Aimbot", "PerfectAura"));
			return true;
		} else if (pitchChange >= 1.0f && pitchChange % 0.1f == 0.0f) {
			if (player.shouldCancel(e, this.getClass().getSimpleName())) {
				e.setCancelled(true);
			}
			player.setViolation(new Violation("Aimbot", "PerfectAura1"));
			return true;
		}
		return false;
	}

	public void Check3(ReceivedPacketEvent e) {
		NessPlayer np = e.getNessPlayer();
		float yawChange = (float) Math.abs(np.getMovementValues().yawDiff);
		float pitchChange = (float) Math.abs(np.getMovementValues().pitchDiff);
		if (yawChange >= 1.0F && yawChange % 0.1F == 0.0F) {
			if (yawChange % 1.0F == 0.0F || yawChange % 10.0F == 0.0F || yawChange % 30.0F == 0.0F) {
				np.setViolation(new Violation("Aimbot", "Pattern1"));
				if (np.shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		} else if (pitchChange >= 1.0F && pitchChange % 0.1F == 0.0F) {
			if (pitchChange % 1.0F == 0.0F || pitchChange % 10.0F == 0.0F || pitchChange % 30.0F == 0.0F) {
				np.setViolation(new Violation("Aimbot", "Pattern2"));
				if (np.shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		}
	}

	public void Check4(ReceivedPacketEvent e) {
		NessPlayer np = e.getNessPlayer();
		double yaw = Math.abs(np.getMovementValues().yawDiff);
		if ((Math.round(Math.abs(yaw)) == Math.abs(yaw) && yaw < 340 && yaw > 0)) {
			np.AimbotPatternCounter = np.AimbotPatternCounter + 1;
			if (np.AimbotPatternCounter > 4) {
				np.setViolation(new Violation("Aimbot", "Pattern3"));
				if (np.shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
				np.AimbotPatternCounter = 0;
			}
		}
	}
}