package com.github.ness.check;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.Utility;

public class BadPackets {

	public static HashMap<String, Integer> repeats = new HashMap<String, Integer>();
	public HashMap<String, Integer> packetsn = new HashMap<String, Integer>();
	int maxpackets = 13;

	public void Check(Player sender, Object packet) {
		if (NESSAnticheat.main == null || sender == null) {
			return;
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(NESSAnticheat.main, () -> {
			int ping = Utility.getPing(sender);
			int maxPackets = maxpackets * (ping / 100);
			int maxPacketsrepeat = 3 * (ping / 100);
			if (ping < 150) {
				maxPackets = maxpackets;
				maxPacketsrepeat = 3;
			}
			if (!packet.toString().contains("Position")) {
				return;
			}

			// System.out.println("Packet: " +packet.toString());
			if (packetsn.get(sender.getName()) == null) {
				return;
			}
			if (Utility.SpecificBlockNear(sender.getLocation(), Material.PORTAL)) {
				return;
			}
			if (packetsn.getOrDefault(sender.getName(), 0) > 9) {
				if (repeats.get(sender.getName()) == null) {
					repeats.put(sender.getName(), 1);
				} else {
					repeats.put(sender.getName(), repeats.get(sender.getName()) + 1);
				}
			}
			if (repeats.get(sender.getName()) == null) {
				return;
			}
			if (packetsn.getOrDefault(sender.getName(), 0) > maxPackets) {
				WarnHacks.warnHacks(sender, "MorePackets", 5, -1.0D, 5, "TooPacket", false);
				return;
			} else if (repeats.get(sender.getName()) > maxPacketsrepeat) {
				WarnHacks.warnHacks(sender, "MorePackets", 5, -1.0D, 5, "BadPacket", false);
				repeats.put(sender.getName(), 0);
			}
			packetsn.remove(sender.getName());
			packetsn.put(sender.getName(), packetsn.getOrDefault(sender.getName(), 0) + 1);
		}, 0);
	}

}
