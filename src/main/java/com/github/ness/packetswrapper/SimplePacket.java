package com.github.ness.packetswrapper;

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
