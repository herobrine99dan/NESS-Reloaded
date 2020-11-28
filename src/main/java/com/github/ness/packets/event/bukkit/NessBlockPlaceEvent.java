package com.github.ness.packets.event.bukkit;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.packets.event.NessEvent;

import lombok.Getter;

public class NessBlockPlaceEvent extends NessBukkitEvent {

    public NessBlockPlaceEvent(BlockPlaceEvent event, NessPlayer player) {
        super(player, event);
    }

}
