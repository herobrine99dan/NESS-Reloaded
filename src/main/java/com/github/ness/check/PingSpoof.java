package com.github.ness.check;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class PingSpoof extends AbstractCheck<ReceivedPacketEvent> {

	public PingSpoof(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(ReceivedPacketEvent.class));
	}
	@Override
	void checkEvent(ReceivedPacketEvent e) {
		NessPlayer np = e.getNessPlayer();
		np.pingspooftimer = System.currentTimeMillis();
		double diff = np.pingspooftimer - np.oldpingspooftimer;
		if (Utility.getPing(np.getPlayer()) > 150 && (diff > 40) && (diff < 70)) {
			// sender.teleport(OldMovementChecks.safeLoc.getOrDefault(sender,
			// sender.getLocation()));
			np.setViolation(new Violation("PingSpoof", ""));
			Utility.setPing(np.getPlayer(), 100);
		}
		np.oldpingspooftimer = np.pingspooftimer;
	}
}
