package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.SendedPacketEvent;

import io.github.retrooper.packetevents.packetwrappers.in.abilities.WrappedPacketInAbilities;
import io.github.retrooper.packetevents.packetwrappers.out.abilities.WrappedPacketOutAbilities;

public class BadPacketsB extends Check {

    private boolean badPacketsAServerSent;
    private boolean badPacketsAClientSent;

    public BadPacketsB(NessPlayer nessPlayer, CheckManager manager) {
        super(BadPacketsB.class, nessPlayer, manager);
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent event) {
        if (event.getPacket().isAbilities()) {
            WrappedPacketInAbilities packet = new WrappedPacketInAbilities(event.getPacket().getRawPacket());
            if (packet.isFlightAllowed())
                badPacketsAClientSent = true;
        } else {
            badPacketsAClientSent = false;
            badPacketsAServerSent = false;
        }
        if (!badPacketsAServerSent && badPacketsAClientSent) {
            flag(event);
        }
    }

    @Override
    public void onEveryPacket(SendedPacketEvent event) {
        if (event.getPacket().isServerAbilities()) {
            WrappedPacketOutAbilities packet = new WrappedPacketOutAbilities(event.getPacket().getRawPacket());
            if (packet.isFlightAllowed())
                badPacketsAServerSent = true;
        }
    }

}
