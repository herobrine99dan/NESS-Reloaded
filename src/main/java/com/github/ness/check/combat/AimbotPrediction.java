package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class AimbotPrediction extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	public AimbotPrediction(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	private float lastYaw, lastPitch, lastYawDiff, lastPitchDiff;

	@Override
	protected void checkPacket(Packet packet) {
		if (!packet.isPacketType(this.packetTypeRegistry().playInFlying()) || player().isTeleported()) {
			return;
		}
		PlayInFlying wrapper = packet.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		if (!wrapper.hasLook()) {
			return;
		}
		float yaw = wrapper.yaw();
		float pitch = wrapper.pitch();
		float yawDiff = yaw - lastYaw;
		float pitchDiff = pitch - lastPitch;
		theCheck(packet, yawDiff, pitchDiff);
		lastYaw = wrapper.yaw();
		lastPitch = wrapper.pitch();
		lastYawDiff = yawDiff;
		lastPitchDiff = pitchDiff;
	}

	private void theCheck(Packet packet, float yawDiff, float pitchDiff) {
		float xDelta = (float) round((yawDiff - lastYawDiff) / this.player().getGcd());
		float yDelta = (float) round((pitchDiff + lastPitchDiff) / this.player().getGcd());
		this.player().sendDevMessage("xDelta: " + xDelta + " yDelta: " + yDelta);
	}
	
	private float round(float n) {
		float places = 100.0f;
		return Math.round(n*places)/places;
	}
}
