package com.github.ness.check.combat;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;
import com.github.ness.utility.MathUtils;

public class AimbotPattern extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	private float lastPitchDelta;
	private double lastPitch, buffer;

	public AimbotPattern(PacketCheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
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
		lastPitch = wrapper.pitch();
	}
	
	/**
	 * [If you say i'm skidding, i will be really really sad :( ]
	 * From AntiHaxerman (https://github.com/Tecnio/AntiHaxerman/blob/master/src/main/java/me/tecnio/antihaxerman/check/impl/combat/aim/AimF.java)
	 * @author Tecnio
	 */

	private void process(Packet packet) {
		PlayInFlying wrapper = packet.toPacketWrapper(this.packetTypeRegistry().playInFlying());
		float pitchDelta = (float) Math.abs(wrapper.pitch() - lastPitch);
		float gcdPitch = (float) MathUtils.gcdRational(pitchDelta, lastPitchDelta);
		float pitchAdapted = (wrapper.pitch() % gcdPitch);
		if (Math.abs(pitchAdapted) < 1e-4 && pitchDelta > 5) {
			if (++buffer > 2) {
				flag("adapted: " + pitchAdapted);
			}
		}else if (buffer > 0) {
			buffer -= 0.25;
		}
		lastPitchDelta = pitchDelta;
	}
}
