package com.github.ness.packets.checks;

import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.OldMovementChecks;
import com.github.ness.utility.Utility;

public class BadPackets {
	private static final double MAX_PACKETS_PER_TICK = 1.03;

	/**
	 * From Crescent AntiCheat
	 * https://github.com/davidm98/Crescent/blob/master/src/io/github/davidm98/crescent/detection/checks/movement/packets/PacketsA.java
	 * 
	 * @param p
	 * @param packet
	 */
	public static boolean Check(Player p, Object packet) {
		NessPlayer np = NESSAnticheat.getInstance().getCheckManager().getPlayer(p);
		if (!packet.toString().toLowerCase().contains("position")) {
			return false;
		}

		if (np.lastPacketTime != -1) {
			final long difference = System.currentTimeMillis() - np.lastPacketTime;

			if (difference >= 1000) {
				final int ping = Utility.getPing(p);
				double maxPackets = MAX_PACKETS_PER_TICK;
				if (ping > 100 && ping < 400) {
					float pingresult = ping / 100;
					float toAdd = pingresult / 10;
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
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
					if (perecentageDifference > 7) {
=======
					if (perecentageDifference > 7.0) {
>>>>>>> parent of dc6f74e2... Implementing new options for BadPackets and MorePackets
=======
					if (perecentageDifference > 7.0) {
>>>>>>> parent of dc6f74e2... Implementing new options for BadPackets and MorePackets
=======
					if (perecentageDifference > 7.0) {
>>>>>>> parent of dc6f74e2... Implementing new options for BadPackets and MorePackets
						OldMovementChecks.blockPackets.put(p.getName(), true);
						np.setViolation(new Violation("BadPackets", np.getMovementpacketscounter() + ""));
						return true;
					}
					//You can flag also here
				}

				// Reset everything.
				np.lastPacketTime = System.currentTimeMillis();
				np.movementPackets = 0;
			}
		} else {
			np.lastPacketTime = System.currentTimeMillis();
		}

		np.movementPackets++;
		return false;
	}

}
