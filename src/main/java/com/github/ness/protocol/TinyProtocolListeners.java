package com.github.ness.protocol;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.ness.NESSAnticheat;
import com.github.ness.check.BadPackets;
import com.github.ness.check.PingSpoof;

import io.netty.channel.Channel;

public class TinyProtocolListeners extends TinyProtocol {

	public TinyProtocolListeners(Plugin plugin) {
		super(plugin);
	}

	public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
		if (sender == null || packet == null || NESSAnticheat.main == null) {
			return packet;
		}/*
				if (packetType == PacketType.Play.Client.FLYING) {
			new PingSpoof().Check(sender, packet);
		}else if(packetType == PacketType.Play.Client.POSITION) {
			new BadPackets().Check(sender,packet);
		}
		*/
		//System.out.println("Pacchetto: " + packet.toString());
		if(packet.toString().toLowerCase().contains("position")) {
			BadPackets.Check(sender, packet);
		}else if(packet.toString().toLowerCase().contains("flying")) {
			PingSpoof.Check(sender, packet);
		}
		return packet;
	}
}
