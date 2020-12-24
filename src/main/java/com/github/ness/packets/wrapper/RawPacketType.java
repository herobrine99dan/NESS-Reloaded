package com.github.ness.packets.wrapper;

import com.github.ness.packets.Packet;
import com.github.ness.packets.PacketType;

/**
 * Abstract implementation of {@link PacketType} tied to a specific class
 *
 * @param <P> the packet type
 */
public abstract class RawPacketType<P> implements PacketType<P> {

	private final Class<?> packetClass;

	protected RawPacketType(Class<?> packetClass) {
		this.packetClass = packetClass;
	}

	@Override
	public final boolean isPacket(Packet packet) {
		return packetClass.isInstance(packet.getRawPacket());
	}

	@Override
	public final P convertPacket(Packet packet) {
		return convertPacket(packet.getRawPacket());
	}

	protected abstract P convertPacket(Object packet);

}
