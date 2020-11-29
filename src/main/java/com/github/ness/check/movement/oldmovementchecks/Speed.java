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

public class Speed extends Check {

    private int preVL;

    public Speed(NessPlayer player, CheckManager manager) {
        super(Speed.class, player, true, Duration.ofSeconds(1).toMillis(), manager);
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
        ImmutableLoc to = values.getTo();
        ImmutableLoc from = values.getFrom();
        NessPlayer nessPlayer = this.player();
        MovementValues movementValues = this.player().getMovementValues();
        final double dist = from.distance(to);
        double hozDist = dist - (to.getY() - from.getY());
        if (to.getY() < from.getY())
            hozDist = dist - (from.getY() - to.getY());
        double maxSpd = 0.43;
        if (player().milliSecondTimeDifference(PlayerAction.VELOCITY) < 1800) {
            hozDist -= Math.abs(player().getLastVelocity().getX());
            hozDist -= Math.abs(player().getLastVelocity().getZ());
        }
        hozDist -= (float) (hozDist / 100.0D * values.getSpeedPotion() * 20.0D);
        if (values.hasBlockNearHead()) {
            maxSpd = 0.50602;
        }
        if(movementValues.isAroundSlabs() || movementValues.isAroundStairs()) {
            maxSpd += 0.1;
        }
        if(movementValues.isAroundIce() || nessPlayer.getTimeSinceLastWasOnIce() < 1000) {
            maxSpd += 0.2;
        }
        if (values.isInsideVehicle() && values.getVehicle().contains("BOAT"))
            maxSpd = 2.787;
        if (hozDist > maxSpd && !values.isAbleFly() && !values.isFlyBypass()
                && nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) >= 2000 && !nessPlayer.isTeleported()) {
            if (nessPlayer.getMovementValues().isGroundAround()) {
                if (nessPlayer.getTimeSinceLastWasOnIce() >= 1000) {
                    if (!values.isInsideVehicle()
                            || (values.isInsideVehicle() && !values.getVehicle().contains("HORSE"))) {
                        if (!values.getFromBlock().isSolid()
                                && !values.getToBlock().isSolid() && nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) > 1000) {
                            if (!values.isTrapdoorNear()) {
                                this.flag(maxSpd + " Dist: " + hozDist,e);
                            }
                        }
                    }
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
