package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.event.bukkit.NessBlockPlaceEvent;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;
import com.github.ness.utility.Utility;

public class ScaffoldFalseTarget extends Check {

    protected ScaffoldFalseTarget(NessPlayer nessPlayer) {
        super(ScaffoldFalseTarget.class, nessPlayer);
    }

    @Override
    public void onBukkitEvent(NessBukkitEvent e) {
        if (!(e instanceof NessBlockPlaceEvent)) {
            return;
        }
        BlockPlaceEvent event = (BlockPlaceEvent) e.getEvent();
        Player player = event.getPlayer();
        Block target = player.getTargetBlock(null, 5);
        if (target == null) {
            return;
        }
        if (Utility
                .getMaterialName(event.getBlock().getWorld()
                        .getBlockAt(event.getBlock().getLocation().subtract(0.0D, 1.0D, 0.0D)).getLocation())
                .contains("AIR")) {
            if (!event.getBlock().getLocation().equals(target.getLocation()) && !event.isCancelled()
                    && target.getType().isSolid() && !target.getType().name().toLowerCase().contains("sign")
                    && !target.getType().toString().toLowerCase().contains("fence")
                    && player.getLocation().getY() > event.getBlock().getLocation().getY()
                    && target.getType().isOccluding()) {
                flag(e);
                // if(player().setViolation(new Violation("Scaffold", "FalseTarget")))
                // event.setCancelled(true);
            }
        }
    }

}
