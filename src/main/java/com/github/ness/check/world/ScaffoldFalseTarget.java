package com.github.ness.check.world;

import com.github.ness.check.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class ScaffoldFalseTarget extends AbstractCheck<BlockPlaceEvent> {

    public ScaffoldFalseTarget(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
        // TODO Auto-generated constructor stub
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
        if (Utility.getMaterialName(event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation().subtract(0.0D, 1.0D, 0.0D)).getLocation()).contains("air")) {
            if (!event.getBlock().getLocation().equals(target.getLocation()) && !event.isCancelled()
                    && target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
                    && !target.getType().toString().toLowerCase().contains("fence")
                    && player.getLocation().getY() > event.getBlock().getLocation().getY() && target.getType().isOccluding()) {
                manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold", "Impossible1"), event);
            }
        }
    }
}
