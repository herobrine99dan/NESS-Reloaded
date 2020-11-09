package com.github.ness.packets.wrappers;

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
		this.vulnerable = (boolean) ReflectionUtility.getDeclaredField(packet, "a");
		this.flying = (boolean) ReflectionUtility.getDeclaredField(packet, "b");
		this.ableFly = (boolean) ReflectionUtility.getDeclaredField(packet, "c");
	}
}
