package com.github.ness.protocol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.NESS;
import com.github.ness.check.BadPackets;
import com.github.ness.check.PingSpoof;

public class DefaultPacketListener {

	public static boolean Executor(Player sender, Object packet) {
		if (sender == null || packet == null) {
			return true;
		}
		// i controlli prima di tutto
		// KillauraBotCheck.Check1(packet,sender);
		if (NESS.main.devMode) {
			// System.out.println("Packet: " + packet.toString());
		}
		if (packet.toString().contains("PacketPlayInFlying")) {
			PingSpoof.Check(sender, packet);
		}else {
			BadPackets.Check(sender, packet);
		}
		return true;
	}

}
