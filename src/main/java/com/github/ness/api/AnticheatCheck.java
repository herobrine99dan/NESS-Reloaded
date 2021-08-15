package com.github.ness.api;

/**
 * A component of an anticheat which is responsible for detecting a certain type of cheat. <br>
 * <br>
 * 
 * 
 * @author A248
 *
 */
public interface AnticheatCheck {

	/**
	 * Gets the name of this check
	 * 
	 * @return the name
	 */
	String getCheckName();
	
	/**
	 * Gets the amount of times a player has violated this check. This value may or may not
	 * be an estimate, and its precise value should not be relied upon. <br>
	 * <br>
	 * If the player is not being tracked for this check, {@code -1} is returned.
	 * 
	 * @param player the anticheat player
	 * @return the violation count for the player, or {@code -1} if the player is not tracked by this check
	 */
	int getViolationCountFor(AnticheatPlayer player);
	
	/**
	 * Flags a player on account of this check. <br>
	 * <br>
	 * The {@link FlagResult} indicates the possible result states of calling this method.
	 * 
	 * @param player the anticheat player
	 * @return a flag result indicating the result state
	 */
	FlagResult flagHack(AnticheatPlayer player);
	
	/**
	 * Gets whether the specified player is tracked by this check. For example, if the player has a bypass
	 * permission and checks are not performed on players with bypass permissions, this will return {@code false}
	 * 
	 * @param player the anticheat player
	 * @return true if the specified player is tracked by this check
	 */
	boolean isTracking(AnticheatPlayer player);
	
}
