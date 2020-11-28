package com.github.ness.packets.event.bukkit;

import org.bukkit.event.Event;

import com.github.ness.NessPlayer;
import com.github.ness.packets.event.NessEvent;

import lombok.Getter;

public abstract class NessBukkitEvent extends NessEvent {
    
    @Getter
    private Event event;

    public NessBukkitEvent(NessPlayer player, Event event) {
        super(player);
        this.event = event;
    }
}
