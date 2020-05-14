package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class BadPackets {
	static int maxpackets = 21;
  /**
   * Simple MaxPackets check
   * @param sender
   * @param packet
 *
   */
	public static void Check(Player sender, Object packet) {
		if (NESSAnticheat.main == null || sender == null) {
          return;
		}
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
			//System.out.println("Sono qua");
			//sender.sendMessage("MaxPackets: " + maxPackets);
			NessPlayer np = InventoryHack.manageraccess.getPlayer(sender);
			if(np==null) {
				return;

			}
			np.setMovementpacketscounter(np.getMovementpacketscounter() + 1);
			//sender.sendMessage("Counter: " + np.getPacketscounter());
			if (np.getMovementpacketscounter() > maxPackets) {
				sender.teleport(OldMovementChecks.safeLoc.getOrDefault(sender, sender.getLocation()));
				InventoryHack.manageraccess.getPlayer(sender).setViolation(new Violation("BadPackets",np.getMovementpacketscounter()+""));
			}
		}, 0);
	}

}
