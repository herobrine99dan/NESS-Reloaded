package com.github.ness.check.killaura.heuristics;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class KillauraFalseFlyingPacket {
	private static HashMap<Player, Long> lastFlying = new HashMap<Player, Long>();

	public static void Check(Object packet, Player p) {
		if (packet.toString().toLowerCase().contains("useentity") && !lastFlying.containsKey(p)) {
			long time = elapsed(lastFlying.get(p));
			if (time < 10L) {
				p.sendMessage("Cheating");
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
