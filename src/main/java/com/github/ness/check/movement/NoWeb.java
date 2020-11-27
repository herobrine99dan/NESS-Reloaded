package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class NoWeb extends Check {

    protected NoWeb(NessPlayer nessPlayer) {
        super(NoWeb.class, nessPlayer);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        NessPlayer nessPlayer = this.player();
        MovementValues values = nessPlayer.getMovementValues();
        double xDiff = Math.abs(nessPlayer.getMovementValues().xDiff);
        double zDiff = Math.abs(nessPlayer.getMovementValues().zDiff);
        final double walkSpeed = values.getWalkSpeed() * 0.85;
        xDiff -= (xDiff / 100.0) * (values.getSpeedPotion() * 20.0);
        zDiff -= (zDiff / 100.0) * (values.getSpeedPotion() * 20.0);
        if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1300) {
            xDiff -= Math.abs(nessPlayer.getLastVelocity().getX());
            zDiff -= Math.abs(nessPlayer.getLastVelocity().getZ());
        }
        if ((xDiff > walkSpeed || zDiff > walkSpeed) && values.getToBlock().getType().contains("WEB")
                && values.getFromBlock().getType().contains("WEB") && !values.isFlyBypass()
                && nessPlayer.milliSecondTimeDifference(PlayerAction.WEBBREAKED) > 1300) {
            flag(e);
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
    }

}
