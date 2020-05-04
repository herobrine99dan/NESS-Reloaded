package com.github.ness.check;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.Utility;
import com.github.ness.Violation;
import com.github.ness.events.PacketInEvent;

public class BadPackets extends AbstractCheck<PacketInEvent> {
	public static HashMap<String, Integer> repeats = new HashMap<String, Integer>();
	public static HashMap<String, Integer> packetsn = new HashMap<String, Integer>();
	static int maxpackets = 13;

	public BadPackets(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PacketInEvent.class));
	}

	@Override
	void checkEvent(PacketInEvent e) {
		Check(e);
	}

	void Check(PacketInEvent e) {
		Player sender = e.getPlayer();
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
				manager.getPlayer(sender).setViolation(new Violation("MorePackets"));
				return;
			} else if (repeats.get(sender.getName()) > maxPacketsrepeat) {
				manager.getPlayer(sender).setViolation(new Violation("BadPackets"));
				repeats.put(sender.getName(), 0);
			}
			packetsn.remove(sender.getName());
			packetsn.put(sender.getName(), packetsn.getOrDefault(sender.getName(), 0) + 1);
		}, 0);
	}

}
