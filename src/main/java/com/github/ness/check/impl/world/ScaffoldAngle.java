package com.github.ness.check.impl.world;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckManager;

public class ScaffoldAngle extends AbstractCheck<BlockPlaceEvent> {
    private static final double MAX_ANGLE = Math.toRadians(90);

    public ScaffoldAngle(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(BlockPlaceEvent.class));
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void checkEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
        if (placedFace == null) {
            return;
        }
        NESSPlayer nessPlayer = this.getNessPlayer(player);
        final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
        float placedAngle = nessPlayer.getMovementValues().getTo().getDirectionVector().toBukkitVector().angle(placedVector);
        if (placedAngle > MAX_ANGLE) {
            manager.getPlayer(event.getPlayer()).setViolation(new Violation("Scaffold", "HighAngle"), event);
        }
    }

}
