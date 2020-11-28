package com.github.ness.check.movement.oldmovementchecks;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class NoFall extends Check {

    private int flags;
    private double serverFallDistance;

    public NoFall(NessPlayer player) {
        super(NoFall.class, player);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        double deltaY = e.getNessPlayer().getMovementValues().yDiff;
        if(deltaY >= 0) {
            serverFallDistance = 0;
        } else if(deltaY < 0) {
            serverFallDistance -= deltaY;
        }
        if((e.getNessPlayer().getMovementValues().getFallDistance() - serverFallDistance) > 0.01) {
            
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
