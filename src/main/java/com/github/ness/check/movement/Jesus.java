package com.github.ness.check.movement;

import org.bukkit.potion.PotionEffectType;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class Jesus extends Check {

    double distmultiplier = 0.75;

    public Jesus(NessPlayer nessPlayer) {
        super(Jesus.class, nessPlayer);
    }

    @Override
    public void onFlying(FlyingEvent e) {
        if (!e.isPosition()) {
            return;
        }
        NessPlayer nessPlayer = this.player();
        double xDist = nessPlayer.getMovementValues().xDiff;
        double zDist = nessPlayer.getMovementValues().zDiff;
        MovementValues values = nessPlayer.getMovementValues();
        double walkSpeed = values.getWalkSpeed() * distmultiplier;
        if (NessAnticheat.getMinecraftVersion() > 1122) {
            walkSpeed = values.getWalkSpeed() * distmultiplier * 1.3;
        }
        xDist -= (xDist / 100.0) * (values.getSpeedPotion() * 20.0);
        zDist -= (zDist / 100.0) * (values.getSpeedPotion() * 20.0);
        if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500) {
            xDist -= Math.abs(nessPlayer.getLastVelocity().getX());
            zDist -= Math.abs(nessPlayer.getLastVelocity().getZ());
        }
        final double yVelocity = Math.abs(values.getServerVelocity().getY()) * 0.32;
        walkSpeed += yVelocity;
        if ((xDist > walkSpeed || zDist > walkSpeed) && values.getToBlock().getType().contains("WATER")
                && values.getFromBlock().getType().contains("WATER") && !values.isFlyBypass()
                && !values.isInsideVehicle() && !values.isAroundLily()) {
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
