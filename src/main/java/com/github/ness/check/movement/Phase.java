package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.ImmutableBlock;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class Phase extends Check {

    public Phase(NessPlayer nessPlayer, CheckManager manager) {
        super(Phase.class, nessPlayer, manager);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.getPacket().isPosition()) {
            return;
        }
        NessPlayer nessPlayer = this.player();
        MovementValues values = nessPlayer.getMovementValues();
        ImmutableBlock block = values.getEyeHeightBlock();
        if (block.isOccluding() && !values.isInsideVehicle() && values.isGroundAround() && !nessPlayer.isTeleported()
                && nessPlayer.getMovementValues().getXZDiff() > 0.195) {
            flag(e);
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
