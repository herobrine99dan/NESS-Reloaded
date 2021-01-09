package com.github.ness.check.required;

import com.github.ness.NessPlayer;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;import com.github.ness.packets.wrapper.PlayInFlying;

public class OnTickEvent extends PacketCheck {

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
