package com.github.ness.check.packet;

import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

public class Freecam extends Check {

    private long lastPosition;
    private int maxDelay = 550;

    public Freecam(NessPlayer player) {
        super(Freecam.class, player,true, 50L);
        // this.maxDelay =
        // this.ness().getMainConfig().getCheckSection().freecam().maxDelay();
        lastPosition = 0;
    }

    @Override
    public void checkAsyncPeriodic() {
        if ((System.nanoTime() - lastPosition) / 1e+6 > maxDelay
                && player().milliSecondTimeDifference(PlayerAction.JOIN) > 2000) {
            runTaskLater(() -> {
                player().getBukkitPlayer().teleport(player().getBukkitPlayer().getLocation().clone().add(0, 0.00001, 0),
                        TeleportCause.PLUGIN);
            }, durationOfTicks(1));
            this.lastPosition = System.nanoTime();
        }
    }

    @Override
    public void onFlying(FlyingEvent e) {}

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
        this.lastPosition = System.nanoTime();
    }
}