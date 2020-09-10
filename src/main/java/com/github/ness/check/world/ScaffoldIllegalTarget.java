package com.github.ness.check.world;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class ScaffoldIllegalTarget extends AbstractCheck<PlayerInteractEvent > {

	public ScaffoldIllegalTarget(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerInteractEvent .class));
	}

	@Override
	protected void checkEvent(PlayerInteractEvent  event) {
		Player player = (Player) event.getPlayer();
		Block target = player.getTargetBlock((Set<Material>) null, 5);
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.isBlockInHand() && !player.isFlying()) {
			if (player.getLocation().getY() > target.getLocation().getY() && !target.isLiquid()
					&& target.getY() % .5 == 0) {
				List<Block> blocks = player.getLastTwoTargetBlocks((Set<Material>) null, 10);
				BlockFace face = null;
				if (blocks.size() > 1) {
					face = blocks.get(1).getFace(blocks.get(0));
				}
				if (event.getBlockFace() != face && target.getType().isSolid() && !Utility.getMaterialName(target.getType()).contains("lever")) {
					this.getNessPlayer(player).setViolation(new Violation("Scaffold"," IllegalTarget"), event);
				}
			}
		}
	}

}
