package com.github.ness.packets.wrapper;

import com.github.ness.packets.PacketType;
import com.github.ness.reflect.ReflectHelper;

public class PlayInArmAnimation {
	
	private PlayInArmAnimation() {
	}

	public static PacketType<PlayInArmAnimation> type(ReflectHelper helper) {
		Class<?> packetClass = helper.getNmsClass("PacketPlayInArmAnimation");

		return new RawPacketType<PlayInArmAnimation>(packetClass) {
			@Override
			public PlayInArmAnimation convertPacket(Object packet) {
				return new PlayInArmAnimation();
			}
		};
	}

}
