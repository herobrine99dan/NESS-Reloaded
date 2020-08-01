package com.github.ness.packets.checks;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.InventoryHack;
import com.github.ness.check.OldMovementChecks;
import com.github.ness.utility.Utility;

public class MorePackets {
	
	public static boolean Check(Player sender, Object packet) {
		int ping = Utility.getPing(sender);
		int maxpackets = 60;
		int maxPackets = maxpackets * (ping / 100);
		if (ping < 150) {
			maxPackets = maxpackets;
		}
		// System.out.println("Sono qua");
		// sender.sendMessage("MaxPackets: " + maxPackets);
		NessPlayer np = NESSAnticheat.getInstance().getCheckManager().getPlayer(sender);
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
			np.setViolation(new Violation("MorePackets", np.getNormalPacketsCounter() + ""));
			return true;
		}
		return false;
	}

}
