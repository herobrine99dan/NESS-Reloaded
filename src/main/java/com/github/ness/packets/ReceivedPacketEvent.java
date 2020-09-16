package com.github.ness.packets;

import com.github.ness.NESSPlayer;
import com.github.ness.packets.wrappers.SimplePacket;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReceivedPacketEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    SimplePacket packet;
    @Getter
    NESSPlayer nessPlayer;
    private boolean cancelled;

    public ReceivedPacketEvent(NESSPlayer nessplayer, SimplePacket packet) {
        super(!Bukkit.isPrimaryThread());
        this.packet = packet;
        this.nessPlayer = nessplayer;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
