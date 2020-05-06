package com.github.ness;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.github.ness.check.KillauraBotCheck;
import com.github.ness.protocol.DefaultPacketListener;

public class Protocols {
	
	public void handleMovement(PacketEvent event) {
		DefaultPacketListener.Executor(event.getPlayer(), event.getPacket(),event.getPacketType());
	}
	
    private void use_entityhandle(PacketEvent event) {
    	KillauraBotCheck.Check1(event);
    }
	
	public Protocols() {
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener) new PacketAdapter(NESSAnticheat.main,
				new PacketType[] { PacketType.Play.Client.POSITION }) {
			public void onPacketReceiving(final PacketEvent event) {
               handleMovement(event);
			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				(PacketListener) new PacketAdapter(NESSAnticheat.main, new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
					public void onPacketReceiving(final PacketEvent event) {
						if (event.getPlayer() == null) {
							return;
						}
						use_entityhandle(event);
					}
				});
		ProtocolLibrary.getProtocolManager().addPacketListener(
				(PacketListener) new PacketAdapter(NESSAnticheat.main, new PacketType[] { PacketType.Play.Client.SETTINGS }) {
					@SuppressWarnings("unchecked")
					public void onPacketReceiving(final PacketEvent event) {

					}
				});
	}
}
