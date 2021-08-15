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

	private float lastYaw, lastPitch, lastYawDiff, lastPitchDiff, lastXDelta, lastYDelta;

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
		calculateAndStoreMouseDeltas(packet, yawDiff, pitchDiff);
		lastYaw = wrapper.yaw();
		lastPitch = wrapper.pitch();
		lastYawDiff = yawDiff;
		lastPitchDiff = pitchDiff;
	}
	
	private void calculateAndStoreMouseDeltas(Packet packet, float yawDiff, float pitchDiff) {
		float xDelta = round((yawDiff - lastYawDiff) / this.player().getGcd());
		float yDelta = round((pitchDiff + lastPitchDiff) / this.player().getGcd());
		this.player().sendDevMessage("xDelta: " + xDelta + " yDelta: " + yDelta);
		// The checks
		costantRotation(xDelta, yDelta);
		lastXDelta = xDelta;
		lastYDelta = yDelta;
	}
	
	private float costantRotation = 0.0f;
	
	private void costantRotation(float xDelta, float yDelta) {
		if(xDelta == 0.0 || yDelta == 0.0) { //Hey, This can't happen normally! Detects costant aiming. (when yawDiff=0.0 then 0.0/gcd=0.0)
			if(++costantRotation > 2) { //Sometimes this happens in Vanilla Minecraft, but it can't happen for a lot of rotations!
				this.flag(" CostantRotation");
			}
		} else if(costantRotation > 0.0) {
			costantRotation -= 0.5;
		}
	}

	private float round(float n) {
		float places = 100.0f;
		return Math.round(n * places) / places;
	}
}
