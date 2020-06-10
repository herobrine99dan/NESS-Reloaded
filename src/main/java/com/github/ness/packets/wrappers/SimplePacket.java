package com.github.ness.packets.wrappers;

public class SimplePacket {
	private Object packet;

	public SimplePacket(Object packet) {
		this.packet = packet;
	}

	public String getName() {
		return this.packet.getClass().getSimpleName();
	}

	public Object getPacket() {
		return this.packet;
	}

}