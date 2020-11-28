package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class InvalidSprint extends Check {

    public InvalidSprint(NessPlayer nessPlayer) {
        super(InvalidSprint.class, nessPlayer);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        MovementValues values = e.getNessPlayer().getMovementValues();
        if (values.isSprinting()) {
            if (values.isBliendnessEffect() || values.getFoodLevel() < 7) {
                flag(e);
                //if(player().setViolation(new Violation("Sprint", "ImpossibleActions"))) event.setCancelled(true);
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) { }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {}

}
