package com.github.ness.check;

import java.util.HashMap;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.packets.events.ReceivedPacketEvent;

public class KillauraFalseFlying extends AbstractCheck<ReceivedPacketEvent> {

	public KillauraFalseFlying(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(ReceivedPacketEvent.class));
	}
	
	private HashMap<NessPlayer, Long> lastFlying = new HashMap<NessPlayer, Long>();
	
	@Override
	void checkEvent(ReceivedPacketEvent e) {
		NessPlayer p = e.getNessPlayer();
		if (e.getPacket().getName().toLowerCase().contains("useentity")) {
			if (!lastFlying.containsKey(p)) {
				return;
			}
			long time = elapsed(lastFlying.get(p));
			if (time < 2L) {
				p.setViolation(new Violation("Killaura", "BadPackets: " + time));
				e.setCancelled(true);
			}
		} else if(e.getPacket().getName().toLowerCase().contains("flying")) {
			if (!lastFlying.containsKey(p)) {
				lastFlying.put(p, System.currentTimeMillis());
				return;
			}
			if (elapsed(lastFlying.get(p)) >= 5L)
				lastFlying.put(p, System.currentTimeMillis());
		}
	}
	
	private static long elapsed(long time) {
		return Math.abs(System.currentTimeMillis() - time);
	}
	
}
