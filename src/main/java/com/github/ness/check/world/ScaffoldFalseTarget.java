package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class ScaffoldFalseTarget extends AbstractCheck<BlockPlaceEvent> {

	public static final CheckInfo<BlockPlaceEvent> checkInfo = CheckInfo
			.eventOnly(BlockPlaceEvent.class);

	public ScaffoldFalseTarget(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(BlockPlaceEvent e) {
        Check1(e);
    }

    public void Check1(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block target = player.getTargetBlock(null, 5);
        if (target == null) {
            return;
        }
        if (Utility.getMaterialName(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0.0D, 1.0D, 0.0D)).getLocation()).contains("AIR")) {
            if (!event.getBlock().getLocation().equals(target.getLocation()) && !event.isCancelled()
                    && target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
                    && !target.getType().toString().toLowerCase().contains("fence")
                    && player.getLocation().getY() > event.getBlock().getLocation().getY() && target.getType().isOccluding()) {
                this.player().setViolation(new Violation("Scaffold", "FalseTarget"), event);
            }
        }
    }
}
