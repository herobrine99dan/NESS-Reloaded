package com.github.ness.check.movement;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpeedPrediction extends AbstractCheck<PlayerMoveEvent> {

    public SpeedPrediction(CheckManager manager) {
        super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
    }

    @Override
    protected void checkEvent(PlayerMoveEvent evt) {
        check(evt);
    }

    private void check(PlayerMoveEvent e) {
        NessPlayer nessPlayer = this.manager.getPlayer(e.getPlayer());

        nessPlayer.lastDeltaXZ = nessPlayer.deltaXZ;
        nessPlayer.deltaXZ = e.getFrom().toVector().setY(0).distance(e.getTo().toVector().setY(0));

        if (nessPlayer.airTicks >= 3 && !e.getPlayer().isFlying() && !Utility.isInWater(e.getPlayer()) && nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1500 && !nessPlayer.isTeleported()) {
            double prediction = nessPlayer.lastDeltaXZ * 0.91F;
            double diff = nessPlayer.deltaXZ - prediction;

            if (diff > 0.026) {
                if (++nessPlayer.speedThreshold > 1) {
                    manager.getPlayer(e.getPlayer()).setViolation(new Violation("Speed", "invalid predicted dist."), e);
                }
            }else nessPlayer.speedThreshold = 0;
        }
    }
}