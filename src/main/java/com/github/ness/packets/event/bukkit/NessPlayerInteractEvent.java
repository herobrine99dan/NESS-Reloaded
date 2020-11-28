package com.github.ness.packets.event.bukkit;

import org.bukkit.event.Event;

import com.github.ness.NessPlayer;

public class NessPlayerInteractEvent extends NessBukkitEvent {

    public NessPlayerInteractEvent(NessPlayer player, Event event) {
        super(player, event);
    }   
}
