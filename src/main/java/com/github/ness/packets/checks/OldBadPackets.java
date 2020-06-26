package com.github.ness.packets.checks;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.InventoryHack;
import com.github.ness.check.OldMovementChecks;
import com.github.ness.utility.ReflectionUtility;
import com.github.ness.utility.Utility;

public class OldBadPackets {
	
	public static void Check(Player sender, Object packet) {
		int ping = Utility.getPing(sender);
		int maxpackets = 21;
		int maxPackets = maxpackets * (ping / 100);
		if (ping < 100) {
			maxPackets = maxpackets;
		}
		// System.out.println("Packet: " +packet.toString());
		if (Utility.SpecificBlockNear(sender.getLocation(), Material.PORTAL)) {
			return;
		}
		// System.out.println("Sono qua");
		// sender.sendMessage("MaxPackets: " + maxPackets);
		NessPlayer np = InventoryHack.manageraccess.getPlayer(sender);
		if (np == null || Utility.hasflybypass(sender)) {
			return;
		}
		np.setMovementpacketscounter(np.getMovementpacketscounter() + 1);
		// sender.sendMessage("Counter: " + np.getPacketscounter());
		if (np.getMovementpacketscounter() > maxPackets) {
			/*
			 * new BukkitRunnable() {
			 * 
			 * @Override public void run() { // What you want to schedule goes here
			 * sender.teleport(OldMovementChecks.safeLoc.getOrDefault(sender,
			 * sender.getLocation())); } }.runTask(NESSAnticheat.main);
			 */
			OldMovementChecks.blockPackets.put(sender.getName(), true);
			InventoryHack.manageraccess.getPlayer(sender)
					.setViolation(new Violation("BadPackets", np.getMovementpacketscounter() + ""));
			return;
		}
		return;
	}

}
