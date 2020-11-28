package com.github.ness.check.world;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.event.bukkit.NessBlockPlaceEvent;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;

public class FastPlace extends Check {
    
    private int blockPlace;

    public FastPlace(NessPlayer nessPlayer) {
        super(FastPlace.class, nessPlayer);
        // TODO Auto-generated constructor stub
    }

    public FastPlace(Class<?> classe, NessPlayer nessPlayer, boolean hasScheduler, long milliSeconds) {
        super(classe, nessPlayer, hasScheduler, milliSeconds);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void onBukkitEvent(NessBukkitEvent ev) {
        if (!(ev instanceof NessBlockPlaceEvent)) {
            return;
        }
        BlockPlaceEvent e = (BlockPlaceEvent) ev.getEvent();
        blockPlace++;
        if (blockPlace > 14) {
            flag(ev);
            //if(player().setViolation(new Violation("FastPlace", "Placing: " + blockPlace))) e.setCancelled(true);
        }
    }

}
