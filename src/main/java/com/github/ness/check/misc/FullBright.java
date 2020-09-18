package com.github.ness.check.misc;

import com.github.ness.check.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.block.BlockPlaceEvent;

public class FullBright extends AbstractCheck<BlockPlaceEvent> {

    public FullBright(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void checkEvent(BlockPlaceEvent e) {
        int level = e.getBlockPlaced().getLightLevel();
        if (level < 4) {
            manager.getPlayer(e.getPlayer()).setViolation(new Violation("FullBright", "Block Placed in Light Free Zone"), e);
        }
    }

}
