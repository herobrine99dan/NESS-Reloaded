package com.github.ness.check;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class BadPackets extends AbstractCheck<ReceivedPacketEvent> {

	public BadPackets(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(ReceivedPacketEvent.class));
	}

	private static final double MAX_PACKETS_PER_TICK = 1.12;

	/**
	 * From Crescent AntiCheat
	 * https://github.com/davidm98/Crescent/blob/master/src/io/github/davidm98/crescent/detection/checks/movement/packets/PacketsA.java
	 * 
	 * @param p
	 * @param packet
	 */
	@Override
	void checkEvent(ReceivedPacketEvent e) {
		NessPlayer np = e.getNessPlayer();
		if (!e.getPacket().getName().toLowerCase().contains("position")) {
			return;
		}

		if (np.lastPacketTime != -1) {
			final long difference = System.currentTimeMillis() - np.lastPacketTime;

			if (difference >= 1000) {
				final int ping = Utility.getPing(np.getPlayer());
				double maxPackets = MAX_PACKETS_PER_TICK;
				if (ping > 100 && ping < 300) {
					float pingresult = ping / 100;
					float toAdd = pingresult / 5;
					maxPackets += toAdd;
				}
				// Check every second.

				/*
				 * The amount of movement packets sent every tick.
				 * 
				 * I have tested the value this gives and the normal client sends about 1 packet
				 * per tick.
				 */
				final double movementsPerTick = ((double) np.movementPackets / ((double) difference / 1000.0)) / 20.0;
				if (movementsPerTick > maxPackets) {
					// The player is sending more packets than allowed.
					final double perecentageDifference = ((movementsPerTick - maxPackets) / maxPackets) * 100.0;

					/*
					 * If the percentage difference is over 7% of what is allowed, the player is
					 * likely to be cheating.
					 */
					if (perecentageDifference > 7.0) {
						np.setViolation(new Violation("BadPackets",
								Math.round(perecentageDifference) + " packets: " + movementsPerTick));
						e.setCancelled(true);
					}
				} else if (movementsPerTick > 0.084 && movementsPerTick < 0.9) {
					double result = np.lastPacketsPerTicks - movementsPerTick;
					if (np.isDevMode()) {
						np.getPlayer().sendMessage("Ticks: " + movementsPerTick + " Result: " + result);
					}
					if (result == 0 || (result > 0 && result < 0.05)) {
						np.setViolation(
								new Violation("BadPackets", "[EXPERIMENTAL] Packets: " + movementsPerTick + " Result: " + result));
						e.setCancelled(true);
					}
				}

				// Reset everything.
				np.lastPacketsPerTicks = (float) movementsPerTick;
				np.lastPacketTime = System.currentTimeMillis();
				np.movementPackets = 0;
			}
		} else {
			np.lastPacketTime = System.currentTimeMillis();
		}

		np.movementPackets++;
	}

}
