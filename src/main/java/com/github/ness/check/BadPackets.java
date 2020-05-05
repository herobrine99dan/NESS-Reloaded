package com.github.ness.check;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.Violation;
import com.github.ness.utility.Utility;

public class BadPackets{
	int maxpackets = 14;

	public void Check(Player sender, PacketContainer packet) {
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
			if (Utility.SpecificBlockNear(sender.getLocation(), Material.PORTAL)) {
				return;
			}
			NessPlayer np = InventoryHack.manageraccess.getPlayer(sender);
			np.setPacketscounter(np.getPacketscounter()+1);
			if (np.getPacketscounter() > 9) {
				np.setPacketsrepeat(np.getPacketsrepeat()+1);
			}
			if (np.getPacketscounter() > maxPackets) {
				InventoryHack.manageraccess.getPlayer(sender).setViolation(new Violation("MorePackets",Arrays.asList(np.getPacketscounter())));
				np.setPacketscounter(0);
				return;
			} else if (np.getPacketsrepeat()> maxPacketsrepeat) {
				InventoryHack.manageraccess.getPlayer(sender).setViolation(new Violation("BadPackets",Arrays.asList(np.getPacketsrepeat())));
				np.setPacketsrepeat(0);
			}
		}, 0);
	}

}
