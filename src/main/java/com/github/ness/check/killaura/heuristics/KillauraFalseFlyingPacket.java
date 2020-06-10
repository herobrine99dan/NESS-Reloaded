package com.github.ness.check.killaura.heuristics;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.github.ness.api.Violation;
import com.github.ness.check.InventoryHack;
import com.github.ness.packets.ReflectionUtility;

public class KillauraFalseFlyingPacket {
	private static HashMap<Player, Long> lastFlying = new HashMap<Player, Long>();

	public static void Check(Object packet, Player p) {
		if (ReflectionUtility.getPacketName(packet).toLowerCase().contains("useentity")) {
			if(!lastFlying.containsKey(p)) {
				return;
			}
			long time = elapsed(lastFlying.get(p));
			if (time < 10L) {
				InventoryHack.manageraccess.getPlayer(p).setViolation(new Violation("Killaura", "BadPackets: " + time));
			}
		} else {
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
