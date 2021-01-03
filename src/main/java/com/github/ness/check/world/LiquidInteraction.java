package com.github.ness.check.world;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;

public class LiquidInteraction extends ListeningCheck<BlockPlaceEvent> {

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public LiquidInteraction(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);

	}

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		if (e.getBlockAgainst().isLiquid() && !e.isCancelled()) {
			String type = e.getBlock().getType().name();
			if (!type.contains("LILY") || !type.contains("SEA")) {
				flagEvent(e);
			}
		}
	}

}
