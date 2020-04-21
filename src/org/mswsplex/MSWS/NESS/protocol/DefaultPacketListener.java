package org.mswsplex.MSWS.NESS.protocol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.NESSPlayer;
import org.mswsplex.MSWS.NESS.checks.BadPackets;
import org.mswsplex.MSWS.NESS.checks.PingSpoof;

import net.minecraft.server.v1_12_R1.PacketPlayInCustomPayload;
import net.minecraft.server.v1_12_R1.PacketPlayInSettings;

public class DefaultPacketListener {

	public static boolean Executor(Player sender, Object packet) {
		if (sender == null || packet == null) {
			return true;
		}
		BadPackets.Check(sender, packet);// i controlli prima di tutto
        //KillauraBotCheck.Check1(packet,sender);
		if (NESS.main.devMode) {
			//System.out.println("Packet: " + packet.toString());
		}
		if (!Bukkit.getVersion().contains("1.12")) {
			NESSPlayer p = NESSPlayer.getInstance(sender);
			p.SetLanguage("Uncompatible Version!");
			p.SetPayLoad("MC|Brand");
			return true;
		}
		if (packet.toString().contains("PacketPlayInSettings")) {
			PacketPlayInSettings pp = (PacketPlayInSettings) packet;
			NESSPlayer p = NESSPlayer.getInstance(sender);
			p.SetLanguage(pp.a());
			// System.out.println("Color Supports:" + pp.d());
			// System.out.println("Client setting for chat: " + pp.c().toString());
		}else if (packet.toString().contains("PacketPlayInCustomPayload")) {
			PacketPlayInCustomPayload pp = (PacketPlayInCustomPayload) packet;
			NESSPlayer p = NESSPlayer.getInstance(sender);
			p.SetPayLoad(pp.a());
		}else if (packet.toString().contains("PacketPlayInFlying")) {
			PingSpoof.Check(sender,packet);
		}//else if (packet.toString().contains("KeepAlive")) {
			//PingSpoof.Check1(sender,packet);
		//}
		return true;
	}
	
}
