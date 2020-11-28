package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity.EntityUseAction;

public class Criticals extends Check {
    
    private boolean onGround;
    
    public Criticals(NessPlayer nessPlayer) {
        super(Criticals.class, nessPlayer);
    }
    
    
    @Override
    public void onFlying(FlyingEvent e) {
        onGround = e.isOnGround();
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
        if (e.getAction().equals(EntityUseAction.ATTACK)) {
            MovementValues values = player().getMovementValues();
            if (!onGround && values.getFallDistance() > 0 && !values.isFlyBypass()
                    && !values.isAroundLiquids()
                    && !values.isInsideVehicle() && !values.isAroundWeb()) {
                NessPlayer np = player();
                if (np.getMovementValues().getTo().getY() % 1.0D == 0.0D) {
                    flag(e);
                }
            }
        }
    }

}
