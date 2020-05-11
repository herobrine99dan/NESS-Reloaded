package com.github.ness.check;

import java.util.List;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class LiquidInteraction extends AbstractCheck<BlockPlaceEvent>{
	
	private final List<String> whitelistedMaterials;
	
	public LiquidInteraction(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
		whitelistedMaterials = manager.getNess().getNessConfig().getCheck(LiquidInteraction.class).getStringList("whitelisted-materials");
	}
	
	@Override
	void checkEvent(BlockPlaceEvent event) {
		if (event.getBlockAgainst().isLiquid()) {
			String type = event.getBlock().getType().name();
			if (!whitelistedMaterials.contains(type)) {
				manager.getPlayer(event.getPlayer()).setViolation(new Violation("LiquidInteraction", type));
			}
		}
	}

}
