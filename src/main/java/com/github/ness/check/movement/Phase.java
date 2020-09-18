package com.github.ness.check.movement;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class Phase extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo
			.eventOnly(PlayerMoveEvent.class);

	public Phase(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Block b = event.getTo().getBlock();
        NessPlayer nessPlayer = this.player();
        if (b.getType().isOccluding() && !event.getPlayer().isInsideVehicle()
                && Utility.groundAround(event.getTo().clone()) && !nessPlayer.isTeleported()
                && nessPlayer.getMovementValues().XZDiff > 0.1) {
            nessPlayer.setViolation(new Violation("Phase", "Walking in a non-Trasparent block"), event);
        }
    }
}
