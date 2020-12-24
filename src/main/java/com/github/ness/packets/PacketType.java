package com.github.ness.packets;

/**
 * An identifier for a kind of packet, which can convert from the general
 * {@link Packet} to the specific type
 *
 * @param <P> the packet type
 */
public interface PacketType<P> {

	/**
	 * Whether the general packet is of this packet type
	 *
	 * @param packet the packet itself
	 * @return true if the packet is of the specified type, false otherwise
	 */
	boolean isPacket(Packet packet);

	/**
	 * Converts a general packet to the specific packet wrapper
	 *
	 * @param packet the packet itself
	 * @return the packet wrapper
	 */
	P convertPacket(Packet packet);
}
