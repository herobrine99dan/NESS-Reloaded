package com.github.ness.check.movement.fly;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class FlyInvalidClientGravity extends Check {
    
    public FlyInvalidClientGravity(NessPlayer player, CheckManager manager) {
        super(FlyInvalidClientGravity.class, player, manager);
    }

    private double lastDeltaY; //TODO use AirTicks

    @Override
    public void onFlying(FlyingEvent e) {
        if(!e.isPosition()) {
            return;
        }
        NessPlayer np = this.player();
        MovementValues values = np.getMovementValues();
        double y = values.yDiff;
        if (values.isFlyBypass() || values.isAbleFly() || values.isAroundLiquids() || values.isInsideVehicle()
                || values.isThereVehicleNear() || values.isAroundWeb() || values.hasBlockNearHead()
                || np.isTeleported()) {
            return;
        }
        double yPredicted = Math.abs((lastDeltaY - 0.08D) * 0.9800000190734863D);
        if (np.milliSecondTimeDifference(PlayerAction.VELOCITY) < 2000) {
            yPredicted = np.getLastVelocity().getY();
        }
        double yResult = Math.abs(y - yPredicted);
        // TODO Needs better ground test
        if (yResult > 0.004D && !values.isGroundAround()) {
            np.sendDevMessage("Predicted: " + (float) yResult + " Y: " + (float) y);
        }
        lastDeltaY = y;
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }
}