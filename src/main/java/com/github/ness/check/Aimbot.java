package com.github.ness.check;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.MovementPlayerData;
import com.github.ness.NessPlayer;
import com.github.ness.Violation;
import com.github.ness.utility.Utilities;
import com.github.ness.utility.Utility;

public class Aimbot extends AbstractCheck<PlayerMoveEvent> {

	public Aimbot(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
		Check1(e);
		Check2(e);
	}

	/**
	 * All changes in pitch should be divisible by a constant. That constant is
	 * determined by your mouse sensitivity in game. By calculating the gcd and
	 * finding that constant, you can detect changes in pitch variation.
	 * 
	 */
	public void Check(PlayerMoveEvent e) {
		int samples = 23;
		int pitchlimit = 10;
		Player p = e.getPlayer();
		NessPlayer player = manager.getPlayer(p);
		float deltaPitch = e.getTo().getPitch() - e.getFrom().getPitch();
		final List<Float> lastDeltaPitches = player.getPitchdelta();

		// ignore if deltaPitch is 0 or >= 10 or if pitch is +/-90.
		if (deltaPitch != 0 && Math.abs(deltaPitch) <= pitchlimit && Math.abs(e.getTo().getPitch()) != 90) {
			lastDeltaPitches.add(Math.abs(deltaPitch));
		}

		if (lastDeltaPitches.size() >= samples) {
			float deltaPitchGCD = Utility.mcdRational(lastDeltaPitches);
			float lastmcdpitch = player.getLastmcdpitch();
			float lastDeltaPitchGCD = (lastmcdpitch != Float.MIN_VALUE) ? lastmcdpitch : deltaPitchGCD;
			float gcdDiff = Math.abs(deltaPitchGCD - lastDeltaPitchGCD);
			// if GCD is significantly different or if GCD is practically unsolvable
			if (gcdDiff > 0.001 || deltaPitchGCD < 0.00001) {
				manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot", "PitchPattern"));
			}
			lastDeltaPitches.clear();
			player.setLastmcdpitch(deltaPitchGCD);
		}

	}
   /**
    * Check for some Aimbot Pattern
    */
	public void Check1(PlayerMoveEvent e) {
		float yawChange = Math.abs(e.getTo().getYaw() - e.getFrom().getYaw());
		float pitchChange = Math.abs(e.getTo().getPitch() - e.getFrom().getPitch());
		if (yawChange >= 1.0f && yawChange % 0.1f == 0.0f) {
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot", "PerfectAura"));
		} else if (pitchChange >= 1.0f && pitchChange % 0.1f == 0.0f) {
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("Aimbot", "PerfectAura1"));
		}
	}
	   /**
	    * Check for some Aimbot Pattern
	    */
	public void Check2(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (event.getFrom().getY() >= event.getTo().getY()) {
			return;
		}
		if (Utilities.isOnStairs(player) || Utilities.isInLiquid(player) || Utilities.isOnWeb(player)) {
			return;// data.hasBeenDamaged()
		}
		if (player.getAllowFlight()) {
			return;
		}
		if (Utility.isOnSlime(player) || Utilities.isOnIce(player, true)) {
			return;
		}
		final double yaw = Math.abs(event.getFrom().getYaw() - event.getTo().getYaw());
		if (yaw > 0.0 && yaw < 360.0) {
			if (yaw >= 5.0) {
				if (yaw % 1.0f == 0.0f) {
					// WarnHacks.warnHacks(event.getPlayer(), "Aimbot", 3, -1.0D, 2, "PerfectAura",
					// false);
					manager.getPlayer(event.getPlayer()).setViolation(new Violation("Aimbot", "PerfectAura2"));
				}
			}
		}
		return;
	}

	public static double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}