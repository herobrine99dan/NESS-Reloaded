package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class FastLadder extends Check {

    public FastLadder(NessPlayer nessPlayer) {
        super(FastLadder.class, nessPlayer);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        NessPlayer nessPlayer = this.player();
        MovementValues movementValues = nessPlayer.getMovementValues();
        if (Utility.isClimbableBlock(movementValues.getToBlock().getType()) && !movementValues.isFlyBypass() && !nessPlayer.isTeleported()) {
            double distance = movementValues.yDiff;
            if (distance > 0.155D && movementValues.getServerVelocity().getY() < 0) {
                flag(e);
                //if(player().setViolation(new Violation("FastLadder", "Dist: " + distance))) event.setCancelled(true);
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {}

}
