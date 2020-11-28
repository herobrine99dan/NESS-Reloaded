package com.github.ness.check.movement.oldmovementchecks;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class EntityFly extends Check {

    public EntityFly(NessPlayer player) {
        super(EntityFly.class, player);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        MovementValues values = player().getMovementValues();
        if (values.isInsideVehicle() && e.isPosition()) {
            if (!values.getVehicle().contains("HORSE")) {
                if (values.yDiff > 0.1 && values.getXZDiff() > 0.1) {
                    this.flag(values.getVehicle(), e);
                }
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {}

}
