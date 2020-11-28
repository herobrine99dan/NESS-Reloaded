package com.github.ness.check.combat;

import java.util.List;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.utility.GraphResult;
import com.github.ness.utility.GraphUtils;
import com.google.common.collect.Lists;

public class CinematicProcessor extends Check {

    public CinematicProcessor(NessPlayer nessPlayer) {
        super(CinematicProcessor.class, nessPlayer);
    }

    private long lastSmooth = 0L, lastHighRate = 0L;
    private double lastDeltaYaw, lastDeltaPitch;

    private final List<Double> yawSamples = Lists.newArrayList();
    private final List<Double> pitchSamples = Lists.newArrayList();

    /**
     * This Code comes from Frequency anticheat (https://github.com/ElevatedDev/Frequency/blob/master/src/main/java/xyz/elevated/frequency/check/impl/aimassist/cinematic/Cinematic.java)
     * @author Elevated
     */
    @Override
    public void onFlying(FlyingEvent event) {
        if(!event.getPacket().isRotation() || !event.isLook()) {
            return;
 
        event.getNessPlayer().setCinematic(cinematic);
    }

}
