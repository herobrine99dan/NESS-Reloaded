package com.github.ness.check.world;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.bukkit.NessBlockBreakEvent;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;
import com.github.ness.packets.event.bukkit.NessInventoryClickEvent;
import com.github.ness.utility.Utility;

public class BlockBreakAction extends Check {

    private static final double MAX_ANGLE = Math.toRadians(90);

    protected BlockBreakAction(NessPlayer nessPlayer) {
        super(BlockBreakAction.class, nessPlayer);
    }

    @Override
    public void onBukkitEvent(NessBukkitEvent ev) {
        NessPlayer nessPlayer = player();
        if (!(ev instanceof NessBlockBreakEvent)) {
            return;
        }
        BlockBreakEvent e = (BlockBreakEvent) ev.getEvent();
        MovementValues values = nessPlayer.getMovementValues();
        Block block = e.getBlock();
        double xDiff = Math.abs(values.getTo().getX() - block.getLocation().getX());
        double yDiff = Math.abs(values.getTo().getY() - block.getLocation().getY());
        double zDiff = Math.abs(values.getTo().getZ() - block.getLocation().getZ());
        // Block target = e.getPlayer().getTargetBlock(Utility.occludingMaterials, 10);
        final double max = 5.4;
        final double placedAngle = Utility.getAngle(e.getPlayer(), block.getLocation(), null);
        if (xDiff > max || yDiff > max || zDiff > max) {
            flag(" HighDistance", ev);
            // if(player().setViolation(new Violation("BreakActions", "HighDistance")))
            // e.setCancelled(true);
        } else if (placedAngle > MAX_ANGLE) {
            flag(" Angle: " + placedAngle, ev);
            // if(player().setViolation(new Violation("BreakActions", "FalseAngle")))
            // e.setCancelled(true);
        } else if(e.getBlock().isLiquid()) {
            flag("Water removed",ev);
        }

    }

}
