package com.github.ness.check.movement.oldmovementchecks;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class Step extends Check {

    public Step(NessPlayer player) {
        super(EntityFly.class, player);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if(!e.isPosition()) {
            return;
        }
        MovementValues values = player().getMovementValues();
        ImmutableLoc from = values.getFrom();
        ImmutableLoc to = values.getTo();
        if (values.isFlyBypass() || values.isAbleFly() || values.isThereVehicleNear()
                || player().isTeleported()) {
            return;
        }
        if (to.getY() - from.getY() > 0.6 && values.isGroundAround() && !values.isAroundSlime()) {
            if (values.getServerVelocity().getY() < 0.43) {
                flag(e);
            }
        } else if (from.getY() - to.getY() > 1.5 && values.getFallDistance() == 0.0 && values.getServerVelocity().getY() < 0.43) {
            flag(e);
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {}

}
