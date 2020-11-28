package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class SpeedFriction extends Check {

    int airTicks;
    double lastDeltaXZ;
    int buffer;

    public SpeedFriction(NessPlayer nessPlayer) {
        super(SpeedFriction.class, nessPlayer);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        NessPlayer nessPlayer = this.player();
        MovementValues values = nessPlayer.getMovementValues();
        if (values.isFlyBypass() || values.isAroundLiquids() || values.isAroundSlime() || values.hasBlockNearHead()) {
            return;
        }
        if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
            return;
        }
        double xzDiff = values.getXZDiff();

        if (!Utility.isMathematicallyOnGround(e.getY())) {
            airTicks++;
        } else {
            airTicks = 0;
        }
        if (airTicks > 2) {
            final double prediction = lastDeltaXZ * 0.91f + (values.isSprinting() ? 0.026 : 0.02);
            final double difference = xzDiff - prediction;
            if (difference > 1e-5) {
                buffer++;
                if (buffer > 4) {
                    this.flag("Diff: " + String.format("%.10f", (double) difference), e);
                }
            } else if (buffer > 0) {
                buffer--;
            }
        }
        this.lastDeltaXZ = xzDiff;
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
