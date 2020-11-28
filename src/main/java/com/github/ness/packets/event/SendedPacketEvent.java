package com.github.ness.packets.event;

import com.github.ness.NessPlayer;
import com.github.ness.packets.Packet;

import lombok.Getter;

public class SendedPacketEvent extends NessEvent {
    @Getter
    Packet packet;

    public SendedPacketEvent(NessPlayer nessplayer, Packet packet) {
        super(nessplayer);
        this.packet = packet;
    }
}
