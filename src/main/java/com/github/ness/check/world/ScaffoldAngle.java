package com.github.ness.check.world;

import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;

public class ScaffoldAngle extends Check {
	private static final double MAX_ANGLE = Math.toRadians(90);

    public ScaffoldAngle(NessPlayer nessPlayer, CheckManager manager) {
        super(ScaffoldAngle.class, nessPlayer, manager);
    }

	@Override
	public void checkEvent(Event e) {
        if (!(e instanceof PlayerMoveEvent)) {
            return;
        }
        BlockPlaceEvent event = (BlockPlaceEvent) e;
		BlockFace placedFace = event.getBlock().getFace(event.getBlockAgainst());
		if (placedFace == null) {
			return;
		}
		NessPlayer nessPlayer = player();
		final Vector placedVector = new Vector(placedFace.getModX(), placedFace.getModY(), placedFace.getModZ());
		float placedAngle = nessPlayer.getMovementValues().getTo().getDirectionVector().toBukkitVector()
				.angle(placedVector);
		if (placedAngle > MAX_ANGLE) {
			this.flag();
		}
	}

}
