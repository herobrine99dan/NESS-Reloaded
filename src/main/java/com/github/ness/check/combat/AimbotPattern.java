package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class AimbotPattern extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private double lastYaw;
	private double lastPitch;

	public AimbotPattern(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		lastYaw = 0;
		lastPitch = 0;
	}

	@Override
	protected void checkPacket(Packet packet) {
		if (!packet.isPacketType(this.packetTypeRegistry().playInFlying()) || player().isTeleported()) {
			return;
		}
		PlayInFlying wrapper = packet.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		if (!wrapper.hasLook()) {
			return;
		}
		process(packet);
		lastYaw = wrapper.yaw();
		lastPitch = wrapper.pitch();
	}

	private void process(Packet packet) {
		PlayInFlying wrapper = packet.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		float yawChange = (float) (wrapper.yaw() - lastYaw);
	}

}
