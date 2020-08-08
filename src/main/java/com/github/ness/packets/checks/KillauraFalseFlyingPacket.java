package com.github.ness.packets.checks;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.api.Violation;
import com.github.ness.check.InventoryHack;
import com.github.ness.utility.ReflectionUtility;

public class KillauraFalseFlyingPacket {
	private static HashMap<Player, Long> lastFlying = new HashMap<Player, Long>();

	public static boolean Check(Object packet, Player p) {
		if (ReflectionUtility.getPacketName(packet).toLowerCase().contains("useentity")) {
			if (!lastFlying.containsKey(p)) {
				return true;
			}
			long time = elapsed(lastFlying.get(p));
			if (time < 2L) {
				NESSAnticheat.getInstance().getCheckManager().getPlayer(p).setViolation(new Violation("Killaura", "BadPackets: " + time));
				return false;
			}
		} else if(ReflectionUtility.getPacketName(packet).toLowerCase().contains("flying")) {
			if (!lastFlying.containsKey(p)) {
				lastFlying.put(p, System.currentTimeMillis());
				return true;
			}
			if (elapsed(lastFlying.get(p)) >= 5L)
				lastFlying.put(p, System.currentTimeMillis());
		}
		return true;
	}

	private static long elapsed(long time) {
		return Math.abs(System.currentTimeMillis() - time);
	}

}
