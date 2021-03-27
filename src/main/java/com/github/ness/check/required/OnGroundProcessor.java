package com.github.ness.check.required;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class OnGroundProcessor extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	public OnGroundProcessor(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkPacket(Packet packet) {
		if (!packet.isPacketType(packetTypeRegistry().playInFlying())) {
			return;
		}
		PlayInFlying wrapper = packet.toPacketWrapper(packetTypeRegistry().playInFlying());
		//Spigot is shit so i must update the onGround boolean alone :(
		this.player().setOnGroundPacket(wrapper.onGround());
	}
}
