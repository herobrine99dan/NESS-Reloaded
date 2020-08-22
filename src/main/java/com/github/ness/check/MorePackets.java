package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class MorePackets extends AbstractCheck<ReceivedPacketEvent> {

	public MorePackets(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(ReceivedPacketEvent.class, 1, TimeUnit.SECONDS));
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
		player.normalPacketsCounter = 0;
	}

	@Override
	void checkEvent(ReceivedPacketEvent e) {
		int ping = Utility.getPing(e.getNessPlayer().getPlayer());
		int maxPackets = 65 + ((ping / 100) * 6);
		if (ping < 200) {
			maxPackets = (int) (65 + ((ping / 100) * 3.2));
		}
		// System.out.println("Sono qua");
		// sender.sendMessage("MaxPackets: " + maxPackets);
		NessPlayer np = e.getNessPlayer();
		if (np == null) {
			return;
		}
		// sender.sendMessage("Counter: " + np.getPacketscounter());
		if (np.normalPacketsCounter++ > maxPackets) {
			/*
			 * new BukkitRunnable() {
			 * 
			 * @Override public void run() { // What you want to schedule goes here
			 * sender.teleport(OldMovementChecks.safeLoc.getOrDefault(sender,
			 * sender.getLocation())); } }.runTask(NESSAnticheat.main);
			 */
			np.setViolation(new Violation("MorePackets", np.normalPacketsCounter + ""));
			e.setCancelled(true);
		}
	}
}
