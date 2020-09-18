package com.github.ness.check.misc;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FullBright extends AbstractCheck<BlockPlaceEvent> {
    
	public static final CheckInfo<BlockPlaceEvent> checkInfo = CheckInfo
			.eventOnly(BlockPlaceEvent.class);

	public FullBright(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}


    @Override
    protected void checkEvent(BlockPlaceEvent e) {
		if (player().isNot(e.getPlayer()))
			return;
        int level = e.getBlockPlaced().getLightLevel();
        if (level < 4) {
            player().setViolation(new Violation("FullBright", "Block Placed in Light Free Zone"), e);
        }
    }

}
