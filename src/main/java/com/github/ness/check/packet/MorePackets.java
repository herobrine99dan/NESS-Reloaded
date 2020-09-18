package com.github.ness.check.packet;

import com.github.ness.check.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

import java.util.concurrent.TimeUnit;

public class MorePackets extends AbstractCheck<ReceivedPacketEvent> {

    int maxPackets;

    public MorePackets(CheckManager manager) {
        super(manager, CheckInfo.eventWithAsyncPeriodic(ReceivedPacketEvent.class, 1, TimeUnit.SECONDS));
        this.maxPackets = this.manager.getNess().getNessConfig().getCheck(this.getClass())
                .getInt("maxpackets", 65);
    }

    @Override
    protected void checkAsyncPeriodic(NessPlayer player) {
        player.normalPacketsCounter = 0;
    }

    @Override
    protected void checkEvent(ReceivedPacketEvent e) {
        int ping = Utility.getPing(e.getNessPlayer().getPlayer());
        int maxPackets = this.maxPackets + ((ping / 100) * 6);
        NessPlayer np = e.getNessPlayer();
        if (np == null) {
            return;
        }
        // sender.sendMessage("Counter: " + np.getPacketscounter());
        if (np.normalPacketsCounter++ > maxPackets && np.nanoTimeDifference(PlayerAction.JOIN) > 2500) {
            /*
             * new BukkitRunnable() {
             *
             * @Override public void run() { // What you want to schedule goes here
             * sender.teleport(OldMovementChecks.safeLoc.getOrDefault(sender,
             * sender.getLocation())); } }.runTask(NESSAnticheat.main);
             */
            np.setViolation(new Violation("MorePackets", np.normalPacketsCounter + ""), e);
            e.setCancelled(true);
        }
    }
}
