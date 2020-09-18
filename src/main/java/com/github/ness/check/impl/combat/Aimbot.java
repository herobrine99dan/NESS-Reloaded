package com.github.ness.check.impl.combat;

import java.util.concurrent.TimeUnit;

import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.GCDUtils;
import com.github.ness.utility.Utility;

public class Aimbot extends AbstractCheck<ReceivedPacketEvent> {

    public Aimbot(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(ReceivedPacketEvent.class));
    }

    @Override
    protected void checkAsyncPeriodic(NESSPlayer player) {

    }

    @Override
    protected void checkEvent(ReceivedPacketEvent e) {
        if (!e.getPacket().getName().toLowerCase().contains("look") || e.getNessPlayer().isTeleported()) {
            return;
        }
        GCDCheck(e);
        Check2(e);
        Check3(e);
    }

    public void GCDCheck(ReceivedPacketEvent event) {
        // float yaw = to.getYaw() - from.getYaw();
        NESSPlayer player = event.getNessPlayer();
        float pitch = (float) Math.abs(player.getMovementValues().pitchDiff);
        if (Math.abs(pitch) >= 10 || Math.abs(pitch) < 0.05 || pitch == 0.0 || !player.isTeleported() || Math.abs(player.getMovementValues().getTo().getPitch()) == 90) {
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
            if ((result > 0.001 || gcd < 0.0001)) {
                // TODO Trying to fix Cinematic Mode
                player.setViolation(new Violation("Aimbot", "GCDCheck" + " GCD: " + gcd), null);
            }
            player.pitchDiff.clear();
            player.lastGCD = gcd;
        }
    }

    public void Check1(ReceivedPacketEvent e) {
        NESSPlayer np = e.getNessPlayer();
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
        NESSPlayer player = e.getNessPlayer();
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
    
	/**
	 * Check for some Aimbot Pattern
	 */
	public void Check3(ReceivedPacketEvent e) {
		NESSPlayer player = e.getNessPlayer();
		float yawChange = (float) Math.abs(player.getMovementValues().yawDiff);
		float pitchChange = (float) Math.abs(player.getMovementValues().pitchDiff);
		if (yawChange > 5 && pitchChange < 0.3) {
			player.setViolation(new Violation("Aimbot", "[Experimental] PerfectAura3"), e);
		} else {
			if(yawChange > 1.0f && Utility.round(yawChange, 100) * 0.1f == yawChange) {
				player.setViolation(new Violation("Aimbot", "[Experimental] PerfectAura4"), e);
			}
		}
	}
    
}