package com.github.ness.check.misc;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class FullBright extends ListeningCheck<BlockPlaceEvent> {

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public FullBright(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		int level = e.getBlockPlaced().getLightLevel();
		if (level < 3) {
			flag();
			//if (player().setViolation(new Violation("FullBright", "")))
			//	e.setCancelled(true);
		}
	}

}
