package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class FastSneak extends Check {

    public FastSneak(NessPlayer nessPlayer, CheckManager manager) {
        super(FastSneak.class, nessPlayer, manager);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        MovementValues values = e.getNessPlayer().getMovementValues();
        if (!e.getPacket().isPosition()) {
            return;
        }
        if(values.isSneaking() && values.getXZDiff() > 0.13) {
            if(e.getNessPlayer().isDevMode()) {
                e.getNessPlayer().sendDevMessage("CHEATS: " + values.getXZDiff());
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
