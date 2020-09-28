package com.github.ness.check.world;

import org.bukkit.event.block.BlockBreakEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class ImpossibleBreak extends ListeningCheck<BlockBreakEvent> {

	public static final ListeningCheckInfo<BlockBreakEvent> checkInfo = CheckInfos.forEvent(BlockBreakEvent.class);

	public ImpossibleBreak(ListeningCheckFactory<?, BlockBreakEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(BlockBreakEvent event) {
		if (event.getBlock().isLiquid()) {
			flagEvent(event);
			//if (player().setViolation(new Violation("ImpossibleBreak", event.getPlayer().getName() + " isn't a god")))
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
