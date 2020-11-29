package com.github.ness.check.combat;

import java.util.ArrayList;
import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;

public class CinematicProcessor extends Check {

    public CinematicProcessor(NessPlayer nessPlayer) {
        super(CinematicProcessor.class, nessPlayer);
    }

    private final List<Double> yawSamples = new ArrayList<Double>();
    private final List<Double> pitchSamples = new ArrayList<Double>();
    private double lastYawDelta, lastYawAccel, lastCinematic;
    private double cinematicTicks;

    @Override
    public void onFlying(FlyingEvent event) {
        if (!event.getPacket().isRotation() || !event.isLook()) {
            return;
        }
        boolean cinematic = false;
        MovementValues values = event.getNessPlayer().getMovementValues();
        final double yawDelta = Math.abs(values.yawDiff);
        final double yawAccel = Math.abs(yawDelta - lastYawDelta);
        final double yawAccelAccel = Math.abs(yawAccelAccel - lastYawAccel);
        final boolean invalidYaw = yawAccelAccel < 0.05 && yawAccelAccel > 0;
        final boolean exponentialYaw = yawAccelAccel < 0.001;
        if (finalSensitivity < 100 && exponentialYaw) {
            cinematicTicks += 3.5;
        } else if (invalidYaw) {
            cinematicTicks += 1.75;
        } else {
            if (cinematicTicks > 0) {
                cinematicTicks -= 0.6;
            }
        }
        if (cinematicTicks > 20) {
            cinematicTicks -= 1.5;
        }
        cinematic = cinematicTicks > 7.5 || ((ticks() - lastCinematic) < 120);
        if(cinematic && cinematicTicks > 7.5) {
            lastCinematic = ticks();
        }
        this.lastYawDelta = yawDelta;
        this.lastYawAccel = yawAccel;
        event.getNessPlayer().setCinematic(cinematic);
    }

}
