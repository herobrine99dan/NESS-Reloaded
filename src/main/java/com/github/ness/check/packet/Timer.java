package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.LongRingBuffer;

public class Timer extends Check {
    
    public Timer(NessPlayer nessPlayer) {
        super(Timer.class, nessPlayer);
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
    public void checkEvent(ReceivedPacketEvent e) {
        NessPlayer nessPlayer = e.getNessPlayer();
        if (!e.getPacket().isPosition() || nessPlayer.isTeleported()) {
            return;
        }
        final long current = System.nanoTime();
        delay.add((long) ((current - lastDelay) / 1e+6));
        if (delay.size() < 40 || nessPlayer.isTeleported()) {
            return;
        }

        final long average = delay.average();
        final float speed = 50.0f / (float) average;
        if (speed > 1.1) {
            this.flag("Timer: " + speed);
        }
        if (nessPlayer.isDebugMode()) {
            nessPlayer.sendDevMessage("Timer: " + speed + " Average: " + average);
        }
        this.lastDelay = current;
    }

}
