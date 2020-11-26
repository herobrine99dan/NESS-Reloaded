package com.github.ness.check.movement.fly;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class FlyInvalidGravity extends Check {

    public FlyInvalidGravity(NessPlayer player) {
        super(FlyInvalidGravity.class, player);
    }

    double maxInvalidVelocity = 1.5;

    @Override
    public void onFlying(FlyingEvent e) {
        NessPlayer np = this.player();
        MovementValues values = np.getMovementValues();
        double y = values.yDiff;
        double yresult = y - values.getServerVelocity().getY();
        if (values.isFlyBypass() || values.isAroundSlime() || values.isAbleFly() || values.isAroundLily()
                || values.isThereVehicleNear()) {
            return;
        }
        double max = maxInvalidVelocity;
        if (np.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2500) {
            y -= Math.abs(np.getLastVelocity().getY());
        }
        if (Math.abs(yresult) > max && !np.isTeleported()) {
            flag(" " + yresult,e);
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }
}