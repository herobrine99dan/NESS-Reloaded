package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.utility.MathUtils;
import com.github.ness.utility.Utility;

public class AimbotGCD extends Check {
    
    private List<Double> pitchDiff = new ArrayList<Double>();
    private double lastGCD = 0;
    private double lastPitchDelta;

    public AimbotGCD(NessPlayer nessPlayer, CheckManager manager) {
        super(AimbotGCD.class, nessPlayer, manager);
    }
    
    @Override
    public void onFlying(FlyingEvent e) {
        NessPlayer player = e.getNessPlayer();
        double pitchDelta = Math.abs(player.getMovementValues().pitchDiff);
        if (Math.abs(pitchDelta) >= 10 || Math.abs(pitchDelta) < 0.05 || pitchDelta == 0.0 || player.isTeleported()
                || Math.abs(player.getMovementValues().getTo().getPitch()) == 90) {
            return;
        }
        pitchDiff.add(pitchDelta);
        if (pitchDiff.size() >= 10) {
            final double gcd = MathUtils.gcdRational(pitchDiff);
            if (lastGCD == 0.0) {
                lastGCD = gcd;
            }
            double result = Math.abs(gcd - lastGCD);
            if(player.isCinematic()) {
                player.sendDevMessage("Cinematic!");
            }
            if (result < 0.007) {
                final double sensitivity = MathUtils.getSensitivity(gcd);
                player.sendDevMessage("GCD: " + Utility.round(gcd, 100) + "Sensitivity: "
                        + Math.round(MathUtils.getSensitivity(gcd) * 200));
                player.setSensitivity(sensitivity);
            }
            if (result > 0.001D || gcd < 1.0E-4D) {
                player.sendDevMessage("GCD: " + Utility.round(gcd, 100) + "Sensitivity: "
                        + Math.round(MathUtils.getSensitivity(gcd) * 200));
            }
            pitchDiff.clear();
            lastGCD = gcd;
        }
        lastPitchDelta = pitchDelta;
    }

}
