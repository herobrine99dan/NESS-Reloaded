package com.github.ness.packets.event;

import com.github.ness.NessPlayer;

public abstract class NessEvent {
    
    private boolean cancelled;
    private NessPlayer player;

    public NessEvent(NessPlayer player) {
        this.player = player;
    }
    
    public NessPlayer getNessPlayer() {
        return this.player;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
