package com.github.ness.check.packet;

import java.util.concurrent.TimeUnit;

import com.github.ness.NessPlayer;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class AbilititiesSpoofed extends AbstractCheck<ReceivedPacketEvent> {

    int maxPackets;
    
	public static final CheckInfo<ReceivedPacketEvent> checkInfo = CheckInfo.eventWithAsyncPeriodic(ReceivedPacketEvent.class, 1, TimeUnit.SECONDS);

	public AbilititiesSpoofed(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
        this.maxPackets = this.ness().getNessConfig().getCheck(this.getClass())
                .getInt("maxpackets", 65);
	}
	
    @Override
    protected void checkEvent(ReceivedPacketEvent e) {
        int ping = Utility.getPing(e.getNessPlayer().getPlayer());
    }

}
