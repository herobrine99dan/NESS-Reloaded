package com.github.ness.check.world;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class ScaffoldIllegalTarget extends AbstractCheck<PlayerInteractEvent> {

	public static final CheckInfo<PlayerInteractEvent> checkInfo = CheckInfo
			.eventOnly(PlayerInteractEvent.class);

	public ScaffoldIllegalTarget(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block target = player.getTargetBlock(null, 5);
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.isBlockInHand() && !player.isFlying()) {
            if (player.getLocation().getY() > target.getLocation().getY() && !target.isLiquid()
                    && target.getY() % .5 == 0) {
                List<Block> blocks = player.getLastTwoTargetBlocks(null, 10);
                BlockFace face = null;
                if (blocks.size() > 1) {
                    face = blocks.get(1).getFace(blocks.get(0));
                }
                if (event.getBlockFace() != face && target.getType().isSolid() && !Utility.getMaterialName(target.getType()).contains("lever")) {
                    this.player().setViolation(new Violation("Scaffold", " IllegalTarget"), event);
                }
            }
        }
    }

}
