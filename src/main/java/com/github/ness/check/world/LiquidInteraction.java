package com.github.ness.check.world;

import java.util.List;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class LiquidInteraction extends AbstractCheck<BlockPlaceEvent> {

    private final List<String> whitelistedMaterials;
    
	public static final CheckInfo<BlockPlaceEvent> checkInfo = CheckInfo
			.eventOnly(BlockPlaceEvent.class);

	public LiquidInteraction(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        whitelistedMaterials = this.ness().getNessConfig().getCheck(LiquidInteraction.class).getStringList("whitelisted-materials");
	}

    @Override
    protected void checkEvent(BlockPlaceEvent e) {
        if (e.getBlockAgainst().isLiquid()) {
            String type = e.getBlock().getType().name();
            if (!whitelistedMaterials.contains(type)) {
                player().setViolation(new Violation("LiquidInteraction", type), e);
            }
        }
    }

}
