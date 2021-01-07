package com.github.ness.packets;

import java.util.UUID;

/**
 * Packet interceptor
 *
 */
public interface PacketInterceptor {

	/**
	 * Whether to drop the specified packet
	 * 
	 * @param uuid the uuid of the player this packet relates to
	 * @param packet the raw packet
	 * @return true to drop the packet, false otherwise
	 */
	boolean shouldDrop(UUID uuid, Object packet) throws Exception;

}
