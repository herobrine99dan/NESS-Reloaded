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
        Block b = event.getTo().clone().add(0, event.getPlayer().getEyeHeight(), 0).getBlock();
        NessPlayer nessPlayer = this.player();
        if (b.getType().isOccluding() && !event.getPlayer().isInsideVehicle()
                && Utility.groundAround(event.getTo().clone()) && !nessPlayer.isTeleported()
                && nessPlayer.getMovementValues().XZDiff > 0.25) {
        	if(player().setViolation(new Violation("Phase", ""))) event.setCancelled(true);
        }
    }
}
