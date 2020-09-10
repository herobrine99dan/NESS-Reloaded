package com.github.ness.check.world;

import java.util.List;

import org.bukkit.event.block.BlockBreakEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;

public class ImpossibleBreak extends AbstractCheck<BlockBreakEvent> {

	private final List<String> whitelistedMaterials;

	public ImpossibleBreak(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(BlockBreakEvent.class));
		whitelistedMaterials = manager.getNess().getNessConfig().getCheck(LiquidInteraction.class)
				.getStringList("whitelisted-materials");
	}

	@Override
	protected void checkEvent(BlockBreakEvent event) {
		if (event.getBlock().isLiquid()) {
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("ImpossibleBreak", ""), event);
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
