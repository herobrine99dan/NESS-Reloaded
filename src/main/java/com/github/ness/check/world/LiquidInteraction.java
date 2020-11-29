package com.github.ness.check.world;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.bukkit.NessBlockPlaceEvent;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;
import com.github.ness.utility.Utility;

public class LiquidInteraction extends Check {

    public LiquidInteraction(NessPlayer nessPlayer, CheckManager manager) {
        super(LiquidInteraction.class, nessPlayer, manager);
    }
    
    @Override
    public void onBukkitEvent(NessBukkitEvent ev) {
        if (!(ev instanceof NessBlockPlaceEvent)) {
            return;
        }
        BlockPlaceEvent e = (BlockPlaceEvent) ev.getEvent();
        if (e.getBlockAgainst().isLiquid()) {
            String type = e.getBlock().getType().name();
            if (!type.contains("LILY") || !type.contains("SEA")) {
                if(!Utility.isItemInHand(e.getPlayer(), "LILY")) {
                    flag(ev);
                }
            }
        }
    }

}
