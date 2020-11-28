package com.github.ness.packets.event.bukkit;

import org.bukkit.event.block.BlockBreakEvent;

import com.github.ness.NessPlayer;
import com.github.ness.packets.event.NessEvent;

import lombok.Getter;

public class NessBlockBreakEvent extends NessBukkitEvent {

    public NessBlockBreakEvent(BlockBreakEvent event, NessPlayer player) {
        super(player, event);

    }

}
