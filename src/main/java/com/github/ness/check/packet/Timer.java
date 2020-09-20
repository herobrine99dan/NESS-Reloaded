package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.packets.ReceivedPacketEvent;
import com.github.ness.utility.Utility;

public class Timer extends AbstractCheck<ReceivedPacketEvent> {
	private double MAX_PACKETS_PER_TICK = 1.12;

	public static final CheckInfo<ReceivedPacketEvent> checkInfo = CheckInfo.eventOnly(ReceivedPacketEvent.class);

	private long lastPacketTime; // Used in BadPackets
	private long movementPackets; // Used in BadPackets
	private float lastPacketsPerTicks;

	public Timer(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		this.MAX_PACKETS_PER_TICK = this.ness().getNessConfig().getCheck(Timer.class).getDouble("maxpackets", 1.12);
	}

	/**
	 * From Crescent AntiCheat
	 * https://github.com/davidm98/Crescent/blob/master/src/io/github/davidm98/crescent/detection/checks/movement/packets/PacketsA.java
	 *
	 * @param p
	 * @param packet
	 */
	@Override
	protected void checkEvent(ReceivedPacketEvent e) {
		NessPlayer nessPlayer = e.getNessPlayer();
		if (!e.getPacket().getName().toLowerCase().contains("position")) {
			return;
		}

		if (lastPacketTime != -1) {
			final long difference = System.currentTimeMillis() - lastPacketTime;

			if (difference >= 1000) {
				final int ping = Utility.getPing(nessPlayer.getPlayer());
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
				final double movementsPerTick = ((double) movementPackets / ((double) difference / 1000.0)) / 20.0;
				if (movementsPerTick > maxPackets && nessPlayer.nanoTimeDifference(PlayerAction.JOIN) > 1300) {
					// The player is sending more packets than allowed.

					/*
					 * If the percentage difference is over 7% of what is allowed, the player is
					 * likely to be cheating.
					 */
					if (!nessPlayer.isTeleported()) {
						nessPlayer.setViolation(new Violation("Timer", " packets: " + movementsPerTick), e);
					}
				} else if (movementsPerTick > 0.084 && movementsPerTick < 0.9) {
					double result = Math.abs(lastPacketsPerTicks - movementsPerTick);
					if (nessPlayer.isDebugMode()) {
						nessPlayer.getPlayer().sendMessage("Ticks: " + movementsPerTick + " Result: " + result);
					}
					if (result < 0.001) {
						nessPlayer.setViolation(new Violation("Timer",
								"[EXPERIMENTAL] Packets: " + movementsPerTick + " Result: " + result), null);
					}
				}

				// Reset everything.
				lastPacketsPerTicks = (float) movementsPerTick;
				lastPacketTime = System.currentTimeMillis();
				movementPackets = 0;
			}
		} else {
			lastPacketTime = System.currentTimeMillis();
		}

		movementPackets++;
	}

}
