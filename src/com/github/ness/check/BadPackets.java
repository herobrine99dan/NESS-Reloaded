package com.github.ness.check;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.NESSPlayer;
import org.mswsplex.MSWS.NESS.PlayerManager;
import org.mswsplex.MSWS.NESS.WarnHacks;

import com.github.ness.Utility;

public class BadPackets {

	public static HashMap<String, Integer> repeats = new HashMap<String, Integer>();

	public static void Check(Player sender, Object packet) {
		if(NESS.main==null || sender == null) {
			return;
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(NESS.main, () -> {
			int ping = PlayerManager.getPing(sender);
			int maxPackets = NESS.main.maxpackets * (ping/100);
			int maxPacketsrepeat = 3 * (ping/100);
			if(ping<150) {
				maxPackets = NESS.main.maxpackets;
				maxPacketsrepeat = 3;
			}
			NESSPlayer p = NESSPlayer.getInstance(sender);
			if (!packet.toString().contains("Position")) {
				return;
			}

	//System.out.println("Packet: " +packet.toString());
			if (p == null) {
				return;
			}
			if(Utility.SpecificBlockNear(sender.getLocation(), Material.PORTAL)) {
				return;
			}
			if (p.getPacketsNumber() > 9) {
				if (repeats.get(sender.getName()) == null) {
					repeats.put(sender.getName(), 1);
				} else {
					repeats.put(sender.getName(), repeats.get(sender.getName()) + 1);
				}
			}
			if (NESS.main.debugMode) {
				sender.sendMessage("Packets:" + p.getPacketsNumber());
			}
			if(repeats.get(sender.getName())==null) {
				return;
			}
			if (p.getPacketsNumber() > maxPackets) {
                WarnHacks.warnHacks(sender, "MorePackets", 5, -1.0D, 5, "TooPacket", false);
                if(NESS.main.devMode) {
                	sender.sendMessage("Your ping: " + PlayerManager.getPing(sender) + " - your max packets: " + maxPackets);
                }
				return;
			}else if(repeats.get(sender.getName()) > maxPacketsrepeat){
				WarnHacks.warnHacks(sender, "MorePackets", 5, -1.0D, 5, "BadPacket", false);
                if(NESS.main.devMode) {
                	sender.sendMessage("Your ping: " + PlayerManager.getPing(sender) + " - your max packets repeat: " + maxPacketsrepeat);
                }
				repeats.put(sender.getName(), 0);
			}
			p.SetPacketsNumber(p.getPacketsNumber() + 1);
		},0);
	}

}
