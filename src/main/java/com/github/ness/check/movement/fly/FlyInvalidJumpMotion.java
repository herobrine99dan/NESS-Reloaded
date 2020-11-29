package com.github.ness.check.movement.fly;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class FlyInvalidJumpMotion extends Check {

    public FlyInvalidJumpMotion(NessPlayer player, CheckManager manager) {
        super(FlyInvalidJumpMotion.class, player, manager);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if(!e.isPosition()) {
            return;
        }
        NessPlayer player = e.getNessPlayer();
        MovementValues values = player.getMovementValues();
        ImmutableLoc to = values.getTo();
        ImmutableLoc from = values.getFrom();
        double yDiff = to.getY() - from.getY();
        NessPlayer nessPlayer = this.player();
        MovementValues movementValues = nessPlayer.getMovementValues();
        if (movementValues.isAroundSlabs() || movementValues.isAroundLiquids() || movementValues.isAroundSnow()
                || movementValues.isAroundChest() || movementValues.isAroundPot() || movementValues.isAroundDetector()
                || movementValues.isAroundBed() || movementValues.isAroundLadders() || movementValues.isAroundChorus()
                || movementValues.isAroundSea() || movementValues.isAroundIce() || movementValues.isAroundSlime()
                || movementValues.isFlyBypass() || movementValues.hasBlockNearHead()) {
            return;
        }
        if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1300) {
            yDiff -= Math.abs(nessPlayer.getLastVelocity().getY());
        }
        // !player.getNearbyEntities(4, 4, 4).isEmpty()
        if (yDiff > 0 && !movementValues.isInsideVehicle()) {
            if (movementValues.getServerVelocity().getY() == 0.42f
                    && !Utility.isMathematicallyOnGround(movementValues.getTo().getY())
                    && Utility.isMathematicallyOnGround(movementValues.getFrom().getY())) {
                double yResult = Math.abs(yDiff - movementValues.getServerVelocity().getY());
                if (yResult != 0.0 && nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) > 1700
                        && nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 1700) {
                    flag(" yResult: " + yResult + "  yDiff: " + yDiff, e);
                    // if(player().setViolation(new Violation("Fly", "InvalidJumpMotion yResult: " +
                    // yResult + " yDiff: " + yDiff))) event.setCancelled(true);
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