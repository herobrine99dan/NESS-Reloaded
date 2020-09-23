package com.github.ness.check.world;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class ScaffoldAngle extends AbstractCheck<BlockPlaceEvent> {
    private static final double MAX_ANGLE = Math.toRadians(90);
    
	public static final CheckInfo<BlockPlaceEvent> checkInfo = CheckInfo
			.eventOnly(BlockPlaceEvent.class);

	public ScaffoldAngle(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}    

    @Override
    protected void checkEvent(BlockPlaceEvent event) {
        BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
        if (placedFace == null) {
            return;
        }
        NessPlayer nessPlayer = player();
        Material material = event.getPlayer().getItemInHand().getType();
        if((event.getBlockPlaced().getType() != material) && material.isSolid() && material.isOccluding()) {
			if(player().setViolation(new Violation("Scaffold", "Impossible"))) event.setCancelled(true);

        	return;
        }
        final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
        float placedAngle = nessPlayer.getMovementValues().getTo().getDirectionVector().toBukkitVector().angle(placedVector);
        if (placedAngle > MAX_ANGLE) {
			if(player().setViolation(new Violation("Scaffold", "HighAngle"))) event.setCancelled(true);
        }
    }

}
