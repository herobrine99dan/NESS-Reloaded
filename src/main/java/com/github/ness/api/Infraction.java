package com.github.ness.api;

/**
 * An infraction of a specific check. Implementations are required to be immutable and thread safe.
 * 
 * @author A248
 *
 */
public interface Infraction {

	/**
	 * Gets the check the infraction is for
	 * 
	 * @return the check, never {@code null}
	 */
	AnticheatCheck getCheck();
	
	/**
	 * Gets the player who flagged the check
	 * 
	 * @return the player, never {@code null}
	 */
	AnticheatPlayer getPlayer();

	/**
	 * Gets the total amount of infractions of the player with regards to this check, including past infractions
	 * 
	 * @return the total amount of infractions of the player with respect to the check, always positive
	 */
	int getCount();
	
	/**
	 * Determines equality with another object consistent with this infraction's details
	 * 
	 * @param object the other object
	 * @return true if the object is equal, false otherwise
	 */
	@Override
	boolean equals(Object object);
	
}
