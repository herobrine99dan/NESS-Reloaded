package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;

public class TimerB extends PacketCheck {

    public static final CheckInfo checkInfo = CheckInfos.forPackets();

    private long lastTime = System.currentTimeMillis();
    private short balance = 0;

    public TimerB(PacketCheckFactory<?> factory, NessPlayer player) {
        super(factory, player);
    }

    public interface Config {
    }
    
    @Override
    protected void checkPacket(Packet packet) {
        NessPlayer nessPlayer = player();
        if (!packet.getRawPacket().getClass().getSimpleName().toLowerCase().contains("position")) return;
        if (nessPlayer.isTeleported() || nessPlayer.isHasSetback()) {
            return;
        }
        long CTime = System.currentTimeMillis();
        long LTime = this.lastTime;

        long time = CTime - LTime;

        this.balance++;

        if (time >= 1000) {
            if (this.balance/(time/1000D) > 21.0D) {
                this.flag("BasicTimerB rate: " + this.balance/(time/1000D));
            }
            this.balance = 0;
            this.lastTime = CTime;
        }
    }
}
