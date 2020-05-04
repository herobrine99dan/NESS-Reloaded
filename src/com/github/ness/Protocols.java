package com.github.ness;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.github.ness.protocol.DefaultPacketListener;

public class Protocols {
	
	public void handle(PacketEvent event) {
		DefaultPacketListener.Executor(event.getPlayer(), event.getPacket(),event.getPacketType());
	}
	
	public Protocols() {
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(NESSAnticheat.main,
				new PacketType[] { PacketType.Play.Client.POSITION }) {
			public void onPacketReceiving(final PacketEvent event) {

			}
		});
	}
}
