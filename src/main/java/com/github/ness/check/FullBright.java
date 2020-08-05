package com.github.ness.check;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class FullBright extends AbstractCheck<BlockPlaceEvent> {

	public FullBright(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	void checkEvent(BlockPlaceEvent e) {
		int level = e.getBlockPlaced().getLightLevel();
		if(level < 4) {
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("FullBright", "Block Placed in Light Free Zone"));
		}
	}
	
}
