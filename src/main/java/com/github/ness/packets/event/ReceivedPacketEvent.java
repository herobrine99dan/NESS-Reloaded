package com.github.ness.packets.event;

import com.github.ness.NessPlayer;
import com.github.ness.packets.Packet;

import lombok.Getter;
import lombok.Setter;

public class ReceivedPacketEvent extends NessEvent {
    @Getter
    Packet packet;

    public ReceivedPacketEvent(NessPlayer nessplayer, Packet packet) {
        super(nessplayer);
        this.packet = packet;
    }
    
    public void process() {}
}