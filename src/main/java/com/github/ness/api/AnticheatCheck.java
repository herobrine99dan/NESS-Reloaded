package com.github.ness.api;

/**
 * A component of an anticheat which is responsible for detecting a certain type of hack
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
	
}
