package com.github.ness.packets.wrappers;

import com.github.ness.NESSAnticheat;
import com.github.ness.utility.ReflectionUtility;

import lombok.Getter;

public class PacketPlayAbilities extends SimplePacket {
	@Getter
	private boolean vulnerable;
	@Getter
	private boolean flying;
	@Getter
	private boolean ableFly;

	public PacketPlayAbilities(Object packet) {
		super(packet);
		if(NESSAnticheat.getMinecraftVersion() > 1152) {
			this.vulnerable = false;
			this.flying = (boolean) ReflectionUtility.getDeclaredField(packet, "a");
			this.ableFly = false;
			return;
		}
		this.vulnerable = (boolean) ReflectionUtility.getDeclaredField(packet, "a");
		this.flying = (boolean) ReflectionUtility.getDeclaredField(packet, "b");
		this.ableFly = (boolean) ReflectionUtility.getDeclaredField(packet, "c");
	}
}
