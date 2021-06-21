package com.github.ness.packets.wrapper;

import com.github.ness.packets.PacketType;

/**
 * Registry of {@link PacketType}s corresponding to packet wrappers
 *
 */
public abstract class PacketTypeRegistry {

	protected PacketTypeRegistry() {

	}

	protected abstract <P> PacketType<P> getPacketType(Class<P> packetTypeClass);

	/*
	 * Packet type retrieval methods
	 */

	public final PacketType<PlayInFlying> playInFlying() {
		return getPacketType(PlayInFlying.class);
	}
	
	public final PacketType<PlayInEntityAction> playInEntityAction() {
		return getPacketType(PlayInEntityAction.class);
	}
}
