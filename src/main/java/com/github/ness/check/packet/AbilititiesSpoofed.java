package com.github.ness.check.packet;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class AbilititiesSpoofed extends ListeningCheck<ReceivedPacketEvent> {

    int maxPackets;
    
	public static final ListeningCheckInfo<ReceivedPacketEvent> checkInfo = CheckInfos
			.forEventWithAsyncPeriodic(ReceivedPacketEvent.class, Duration.ofSeconds(1));

	public AbilititiesSpoofed(ListeningCheckFactory<?, ReceivedPacketEvent> factory, NessPlayer player) {
		super(factory, player);
        this.maxPackets = this.ness().getNessConfig().getCheck(this.getClass())
                .getInt("maxpackets", 65);
	}
	
    @Override
    protected void checkEvent(ReceivedPacketEvent e) {
        int ping = Utility.getPing(e.getNessPlayer().getPlayer());
    }

}
