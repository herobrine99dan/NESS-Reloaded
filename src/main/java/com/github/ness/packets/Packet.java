package com.github.ness.packets;

import java.util.UUID;

/**
 * An individual sent packet which is associated with a player, and can be cancelled. <br>
 * <br>
 * Instances of this interface should not be persisted in collections.
 * 
 * @author A248
 *
 */
public interface Packet {

	/**
	 * Cancels this packet. Will prevent it from being sent
	 * 
	 */
	void cancel();

	/**
	 * Gets the UUID of the player associated with this packet
	 * 
	 * @return the uuid of the associated player
	 */
	UUID getAssociatedUniqueId();

	/**
	 * Gets the raw packet object
	 * 
	 * @return the NMS packet
	 */
	Object getRawPacket();

	// Reflection helpers
}
