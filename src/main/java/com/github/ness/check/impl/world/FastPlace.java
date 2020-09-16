package com.github.ness.check.impl.world;

import com.github.ness.check.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.concurrent.TimeUnit;

public class FastPlace extends AbstractCheck<BlockPlaceEvent> {

    int max;

    public FastPlace(CheckManager manager) {
        super(manager, CheckInfo.eventWithAsyncPeriodic(BlockPlaceEvent.class, 1, TimeUnit.SECONDS));
        this.max = this.manager.getNess().getNessConfig().getCheck(this.getClass())
                .getInt("maxblockplaced", 14);
    }

    @Override
    protected void checkAsyncPeriodic(NESSPlayer player) {
        player.blockPlace = 0;
    }

    @Override
    protected void checkEvent(BlockPlaceEvent e) {
        Check(e);
    }

    /**
     * A Simple FastPlace check
     *
     * @param event
     */
    public void Check(BlockPlaceEvent e) {
        NESSPlayer player = manager.getPlayer(e.getPlayer());
        player.blockPlace++;
        if (player.blockPlace > max) {
            player.setViolation(new Violation("FastPlace", "Placing: " + player.blockPlace), e);
        }
    }

}
