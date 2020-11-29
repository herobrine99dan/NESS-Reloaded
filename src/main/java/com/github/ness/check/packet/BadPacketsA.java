package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.FlyingEvent;

public class BadPacketsA extends Check {

    protected BadPacketsA(NessPlayer nessPlayer, CheckManager manager) {
        super(BadPacketsA.class, nessPlayer, manager);
    }

    @Override
    public void onFlying(FlyingEvent event) {
        if (event.isLook() && event.getPacket().isRotation()) {
            if (Math.abs(event.getPitch()) > 90f) {
                flag(event);
            }
        }
    }

}
