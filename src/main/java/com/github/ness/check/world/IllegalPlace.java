package com.github.ness.check.world;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.Location;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class IllegalPlace extends ListeningCheck<BlockPlaceEvent> {

	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEvent(BlockPlaceEvent.class);

	public IllegalPlace(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);

	}

	@Override
	protected void checkEvent(BlockPlaceEvent e) {
		if(e.isCancelled()) return;
		if (e.getBlockAgainst().isLiquid()) {
			String type = e.getBlock().getType().name();
			if (!type.contains("LILY") && !type.contains("SEA")) {
				flagEvent(e, "LiquidPlacement");
				return;
			}
		}
		if (e.getBlockAgainst().getType().name().contains("AIR")) {
			flagEvent(e, "AirPlacement");
			return;
		}
		Location blockFace = e.getBlockAgainst().getLocation().clone().subtract(e.getBlockPlaced().getLocation());
		if(Math.abs(blockFace.getX()) == 1 && Math.abs(blockFace.getZ()) == 1) {
			flagEvent(e, "DiagonalPlacement");
			return;
		}
	}

}
