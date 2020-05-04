package com.github.ness.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Used to abstract NMS usage
 * 
 * @author A248
 *
 */
public interface NMSHandler {

	/**
	 * Creates a NPC/fake player for a specific user.
	 * 
	 * @param target the player who will see the fake player
	 * @param location where to spawn the NPC
	 * @return the uuid of the spawned NPC
	 */
	NPC createFakePlayer(Player target, Location location);
	
}
