package com.github.ness.packets.checks;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.InventoryHack;
import com.github.ness.check.OldMovementChecks;
import com.github.ness.utility.Utility;

public class MorePackets {
	
	public static boolean Check(Player sender, Object packet) {
		int ping = Utility.getPing(sender);
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
		int maxpackets = 65;
=======
		int maxpackets = 60;
>>>>>>> parent of dc6f74e2... Implementing new options for BadPackets and MorePackets
=======
		int maxpackets = 60;
>>>>>>> parent of dc6f74e2... Implementing new options for BadPackets and MorePackets
=======
		int maxpackets = 60;
>>>>>>> parent of dc6f74e2... Implementing new options for BadPackets and MorePackets
		int maxPackets = maxpackets * (ping / 100);
		if (ping < 150) {
			maxPackets = maxpackets;
		}
		// System.out.println("Packet: " +packet.toString());
		if (Utility.specificBlockNear(sender.getLocation(), Material.PORTAL)) {
			return false;
		}
		// System.out.println("Sono qua");
		// sender.sendMessage("MaxPackets: " + maxPackets);
		NessPlayer np = InventoryHack.manageraccess.getPlayer(sender);
		if (np == null || sender.isInsideVehicle()) {
			return false;
		}
		np.setNormalPacketsCounter(np.getNormalPacketsCounter() + 1);
		// sender.sendMessage("Counter: " + np.getPacketscounter());
		if (np.getNormalPacketsCounter() > maxPackets) {
			/*
			 * new BukkitRunnable() {
			 * 
			 * @Override public void run() { // What you want to schedule goes here
			 * sender.teleport(OldMovementChecks.safeLoc.getOrDefault(sender,
			 * sender.getLocation())); } }.runTask(NESSAnticheat.main);
			 */
			OldMovementChecks.blockPackets.put(sender.getName(), true);
			InventoryHack.manageraccess.getPlayer(sender)
					.setViolation(new Violation("MorePackets", np.getNormalPacketsCounter() + ""));
			return true;
		}
		return false;
	}

}
