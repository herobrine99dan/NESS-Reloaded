package com.github.ness.check.movement.oldmovementchecks;

import java.time.Duration;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class NoGround extends Check {

    private int flags;

    public NoGround(NessPlayer player, CheckManager manager) {
        super(NoGround.class, player, true, Duration.ofSeconds(1).toMillis(), manager);
    }

    @Override
    public void checkAsyncPeriodic() {
        flags = 0;
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        MovementValues values = player().getMovementValues();
        ImmutableLoc to = values.getTo();
        NessPlayer nessPlayer = this.player();
        if (values.isFlyBypass() || values.isAbleFly() || values.isThereVehicleNear() || player().isTeleported()
                || values.isAroundLadders() || values.isSneaking() || values.isAroundSlime() || values.isAroundStairs() || nessPlayer.milliSecondTimeDifference(PlayerAction.JOIN) >= 1000) {
            return;
        }
        if (!e.isOnGround() && to.getY() % 1.0 == 0) {
            if (!values.hasBlockNearHead()) {
                int failed = flags++;
                if (failed > 3) {
                    flag(e);
                }
            }
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
