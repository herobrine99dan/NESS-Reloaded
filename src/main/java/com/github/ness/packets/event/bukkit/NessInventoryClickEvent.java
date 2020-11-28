package com.github.ness.packets.event.bukkit;

import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.ness.NessPlayer;
import com.github.ness.packets.event.NessEvent;

import lombok.Getter;

public class NessInventoryClickEvent extends NessBukkitEvent {

    public NessInventoryClickEvent(InventoryClickEvent event, NessPlayer player) {
        super(player, event);

    }
}
