package com.github.ness.check.misc;

import org.bukkit.Location;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

import io.github.retrooper.packetevents.packetwrappers.in.windowclick.WrappedPacketInWindowClick;

public class InventoryHack extends Check {

    public InventoryHack(Class<?> classe, NessPlayer nessPlayer) {
        super(InventoryHack.class, nessPlayer);
    }

    @Override
    public void onFlying(FlyingEvent e) {
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
        if (e.getPacket().isWindowClick()) {
            WrappedPacketInWindowClick packet = new WrappedPacketInWindowClick(e.getPacket().getRawPacket());
            NessPlayer nessPlayer = player();
            MovementValues values = nessPlayer.getMovementValues();
            if (nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) < 1500
                    || nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500
                    || values.isFlyBypass() || this.player().isTeleported()) {
                return;
            }
            if (nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKPLACED) < 100
                    || nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKBROKED) < 100) {
                flag(e);
                return;
            } else if (nessPlayer.milliSecondTimeDifference(PlayerAction.ANIMATION) < 100) {
                flag("MS: " + nessPlayer.milliSecondTimeDifference(PlayerAction.ANIMATION), e);
                return;
            } else if (values.isSprinting() || values.isSneaking()) {
                flag(e);
                return;
            }
            final Location from = values.getTo().toBukkitLocation();

            runTaskLater(() -> {
                Location to = nessPlayer.getBukkitPlayer().getLocation();
                double distance = (Math.abs(to.getX() - from.getX())) + (Math.abs(to.getZ() - from.getZ()));
                if (distance > 0.15) {
                    flag(e);
                }
            }, durationOfTicks(2));
        }
    }

}
