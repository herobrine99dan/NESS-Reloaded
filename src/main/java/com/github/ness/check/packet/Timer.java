package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.utility.LongRingBuffer;

public class Timer extends Check {

    public Timer(NessPlayer nessPlayer, CheckManager manager) {
        super(Timer.class, nessPlayer, manager);
        this.delay = new LongRingBuffer(40);
    }

    private long lastDelay;
    private LongRingBuffer delay;

    @Override
    public void checkAsyncPeriodic() {

    }

    /**
     * Thanks to GladUrBad for a small hint
     */
    @Override
    public void onFlying(FlyingEvent e) {
        NessPlayer nessPlayer = e.getNessPlayer();
        if (nessPlayer.isTeleported()) {
            return;
        }
        if (e.getPacket().isPosition()) {
            final long current = System.nanoTime();
            delay.add((long) ((current - lastDelay) / 1e+6));
            if (delay.size() < 40 || nessPlayer.isTeleported()) {
                return;
            }

            final long average = delay.average();
            final float speed = 50.0f / (float) average;
            if (speed > 1.1) {
                this.flag("Timer: " + speed, e);
            }
            if (nessPlayer.isDebugMode()) {
                nessPlayer.sendDevMessage("Timer: " + speed + " Average: " + average);
            }
            this.lastDelay = current;
        }
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
        // TODO Auto-generated method stub

    }

}
