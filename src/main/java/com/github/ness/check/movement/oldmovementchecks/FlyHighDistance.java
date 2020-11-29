package com.github.ness.check.movement.oldmovementchecks;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class FlyHighDistance extends Check {

    private int preVL;

    public FlyHighDistance(NessPlayer player, CheckManager manager) {
        super(FlyHighDistance.class, player, true, Duration.ofSeconds(1).toMillis(), manager);
    }

    @Override
    public void checkAsyncPeriodic() {
        preVL = 0;
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        MovementValues values = player().getMovementValues();
        double dist = values.getXZDiff();
        if (values.isFlyBypass() || values.isAbleFly() || values.isThereVehicleNear()
                || player().isTeleported()) {
            return;
        }
        if (player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 1600) {
            dist -= Math.abs(player().getLastVelocity().getX()) + Math.abs(player().getLastVelocity().getZ());
        }
        if (!values.isGroundAround() && dist > 0.35 && values.yDiff == 0.0
                && this.player().getTimeSinceLastWasOnIce() >= 1000) {
            if (preVL++ > 1) {
                flag(e);
            }
        } else if (preVL > 0) {
            preVL--;
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
