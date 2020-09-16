package com.github.ness.check.impl.movement;

import com.github.ness.check.CheckManager;
import com.github.ness.NESSPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

public class Phase extends AbstractCheck<PlayerMoveEvent> {

    public Phase(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
    }

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Block b = event.getTo().getBlock();
        NESSPlayer nessPlayer = this.getNessPlayer(event.getPlayer());
        if (b.getType().isOccluding() && !event.getPlayer().isInsideVehicle()
                && Utility.groundAround(event.getTo().clone()) && !nessPlayer.isTeleported()
                && nessPlayer.getMovementValues().XZDiff > 0.1) {
            nessPlayer.setViolation(new Violation("Phase", "Walking in a non-Trasparent block"), event);
        }
    }
}
