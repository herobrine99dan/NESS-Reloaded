package com.github.ness.packets.event;

import org.bukkit.event.Cancellable;

import com.github.ness.NessPlayer;
import com.github.ness.packets.Packet;

import lombok.Getter;

public class ReceivedPacketEvent extends NessEvent {
    @Getter
    Packet packet;
    private boolean cancelled;

    public ReceivedPacketEvent(NessPlayer nessplayer, Packet packet) {
        super(nessplayer);
        this.packet = packet;
    }
    
    public void process() {}
}
