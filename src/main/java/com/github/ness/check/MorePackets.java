package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class MorePackets {
	
	static int maxpackets = 60;

	public static void Check(Player sender, Object packet) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(NESSAnticheat.main, () -> {
			int ping = Utility.getPing(sender);
			int maxPackets = maxpackets * (ping / 100);
			if (ping < 150) {
				maxPackets = maxpackets;
			}
			// System.out.println("Packet: " +packet.toString());
			if (Utility.SpecificBlockNear(sender.getLocation(), Material.PORTAL)) {
				return;
			}
			// System.out.println("Sono qua");
			// sender.sendMessage("MaxPackets: " + maxPackets);
			NessPlayer np = InventoryHack.manageraccess.getPlayer(sender);
			if(np==null) {
				return;
			}
			np.setNormalPacketsCounter(np.getNormalPacketsCounter() + 1);
			// sender.sendMessage("Counter: " + np.getPacketscounter());
			if (np.getNormalPacketsCounter() > maxPackets) {
				InventoryHack.manageraccess.getPlayer(sender)
						.setViolation(new Violation("MorePackets", np.getNormalPacketsCounter() + ""));
			}
		}, 0);
	}

}
