package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class Phase extends Check {

    protected Phase(NessPlayer nessPlayer) {
        super(Phase.class, nessPlayer);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        Block b = event.getTo().clone().add(0, event.getPlayer().getEyeHeight(), 0).getBlock();
        NessPlayer nessPlayer = this.player();
        if (b.getType().isOccluding() && !event.getPlayer().isInsideVehicle()
                && Utility.groundAround(event.getTo().clone()) && !nessPlayer.isTeleported()
                && nessPlayer.getMovementValues().getXZDiff() > 0.25) {
            flagEvent(event);
            //if(player().setViolation(new Violation("Phase", ""))) event.setCancelled(true);
        } 
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {}


}
