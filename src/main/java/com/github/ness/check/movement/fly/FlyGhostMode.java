package com.github.ness.check.movement.fly;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class FlyGhostMode extends Check {

	public FlyGhostMode(NessPlayer player) {
		super(FlyGhostMode.class, player);
	}

    @Override
    public void onFlying(FlyingEvent e) {
        if(!e.isPosition()) {
            return;
        }
        MovementValues values = player().getMovementValues();
        if (values.isDead()) {
            NessPlayer np = this.player();
            if ((np.getMovementValues().getXZDiff() > 0.3 || np.getMovementValues().yDiff > 0.16) && !np.isTeleported()) {
                flag(e);
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {}

}