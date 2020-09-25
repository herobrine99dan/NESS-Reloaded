package com.github.ness.check.world;

import java.util.List;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class LiquidInteraction extends ListeningCheck<BlockPlaceEvent> {


	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public LiquidInteraction(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);

	}

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		if (e.getBlockAgainst().isLiquid()) {
			String type = e.getBlock().getType().name();
			if (!type.contains("LILY") || !type.contains("SEA")) {
				flag();
				//if (player().setViolation(new Violation("LiquidInteraction",
				//		e.getPlayer().getName() + " isn't a god (Block: " + type + ")")))
					//e.setCancelled(true);
			}
		}
	}

}
