package com.github.ness.check.world;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

public class ImpossibleBreak extends AbstractCheck<BlockBreakEvent> {

    
	public static final CheckInfo<BlockBreakEvent> checkInfo = CheckInfo
			.eventOnly(BlockBreakEvent.class);

	public ImpossibleBreak(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(BlockBreakEvent event) {
        if (event.getBlock().isLiquid()) {
            player().setViolation(new Violation("ImpossibleBreak", ""), event);
        }
//		Block target = player.getTargetBlock((Set<Material>) null, 5);
//		boolean bypass = false;
//		if (!event.getBlock().getLocation().equals(target.getLocation()) && target.getType().isSolid()
//				&& !target.getType().name().toLowerCase().contains("sign")
//				&& !target.getType().name().toLowerCase().contains("step")
//				&& !target.getType().name().toLowerCase().contains("chest")
//				&& target.getType() != Material.SNOW
//				&& target.getType() != Material.TORCH
//				&& target.getType() != Material.TNT
//				&& !target.getType().name().toLowerCase().contains("leaves")
//				&& player.getGameMode() != GameMode.CREATIVE
//				&& PlayerManager.timeSince("longBroken", player)>1000
//				&& !bypass) {
//			if (NESS.main.devMode)
//				MSG.tell(player,
//						"&9Dev> &7type: " + target.getType() + " Solid: " + MSG.TorF(target.getType().isSolid()));
//			WarnHacks.warnHacks(player, "Illegal Interaction", 40, 200);
//		}
    }

}
