package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.Utility;

public class MorePackets extends Check {

    private int maxPackets;
    private int serverCrasherMaxPackets;
    int normalPacketsCounter;

    public MorePackets(NessPlayer player) {
        super(MorePackets.class, player,true, 50L);
        //this.maxPackets = this.ness().getMainConfig().getCheckSection().morePackets().maxPackets();
        //this.serverCrasherMaxPackets = this.ness().getMainConfig().getCheckSection().morePackets()
        //        .serverCrasherMaxPackets();
        this.maxPackets = 80;
        this.serverCrasherMaxPackets = 250;
        normalPacketsCounter = -5;
    }

    @Override
    public void checkAsyncPeriodic() {
        normalPacketsCounter = 0;
    }

    @Override
    public void onFlying(FlyingEvent e) {}

    @Override
    public void onUseEntity(UseEntityEvent e) {}

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
        int ping = Utility.getPing(e.getNessPlayer().getBukkitPlayer());
        int maxPackets = this.maxPackets + ((ping / 100) * 6);
        NessPlayer np = e.getNessPlayer();
        if (np == null) {
            return;
        }
        if (np.getMovementValues().isInsideVehicle()) {
            return;
        }
        if (normalPacketsCounter++ > maxPackets && np.milliSecondTimeDifference(PlayerAction.JOIN) > 5000) {
            if (normalPacketsCounter > serverCrasherMaxPackets) {
                e.setCancelled(true);
                np.kickThreadSafe();
                return;
                // np.kickThreadSafe();
            } else {
                flag(e);
            }
        }
    }
}
