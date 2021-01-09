package com.github.ness.check.required;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;

public class OnTickEvent extends PacketCheck {
	
	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	public OnTickEvent(PacketCheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkPacket(Packet packet) {
		final boolean packetName = packet.isPacketType(packetTypeRegistry().playInFlying());
		if (packet.isPacketType(packetTypeRegistry().playInFlying())) {
			player().onClientTick();
		}
	}
}
