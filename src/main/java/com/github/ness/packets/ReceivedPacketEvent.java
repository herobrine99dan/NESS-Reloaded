package com.github.ness.packets;

import org.bukkit.event.Cancellable;

import com.github.ness.NessPlayer;

import lombok.Getter;

public class ReceivedPacketEvent implements Cancellable {
    @Getter
    Packet packet;
    @Getter
    NessPlayer nessPlayer;
    private boolean cancelled;

    public ReceivedPacketEvent(NessPlayer nessplayer, Packet packet) {
        this.packet = packet;
        this.nessPlayer = nessplayer;
    }
    
    public void process() {}
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
