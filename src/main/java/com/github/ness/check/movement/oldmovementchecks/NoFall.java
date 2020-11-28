package com.github.ness.check.movement.oldmovementchecks;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class NoFall extends Check {

    private int flags;
    private double serverFallDistance;

    public NoFall(NessPlayer player) {
        super(NoFall.class, player);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        double deltaY = e.getNessPlayer().getMovementValues().yDiff;
        if (deltaY >= 0) {
            serverFallDistance = 0;
        } else if (deltaY < 0) {
            serverFallDistance -= deltaY;
        }
        double subtraction = Math.abs(e.getNessPlayer().getMovementValues().getFallDistance() - serverFallDistance);
        if (subtraction > 2.3) {
            this.flag("Subtraction: " + (float) subtraction, e);
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
