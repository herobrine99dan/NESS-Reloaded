package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.event.ReceivedPacketEvent;

import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction.PlayerAction;

public class FastSneak extends Check {

    private long lastFlying;
    private boolean sent;
    private int buffer;

    public FastSneak(NessPlayer nessPlayer) {
        super(FastSneak.class, nessPlayer);
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
        final long delay = (long) ((System.nanoTime() - lastFlying) / 1e+6);
        if (e.getPacket().isPosition()) {
            if (sent) {
                if (delay > 40 && delay < 100) {
                    buffer++;
                    if (buffer > 2) {
                        flag(e);
                    }
                } else if (buffer > 0) {
                    buffer--;
                }
            }
            lastFlying = System.nanoTime();
        } else if (e.getPacket().isEntityAction()) {
            WrappedPacketInEntityAction packet = new WrappedPacketInEntityAction(e.getPacket().getRawPacket());
            if (delay < 10 && (packet.getAction().equals(PlayerAction.START_SNEAKING) || packet.getAction().equals(PlayerAction.STOP_SNEAKING))) {
                sent = true;
            }
        }
    }

}
