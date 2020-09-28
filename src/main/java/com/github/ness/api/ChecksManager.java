package com.github.ness.api;

import java.util.Collection;

/**
 * Manager of anticheat checks
 * 
 * @author A248
 *
 */
public interface ChecksManager {

	/**
	 * Gets an immutable copy of all anticheat checks
	 * 
	 * @return an immutable collection copy of all anticheat checks
	 */
	Collection<? extends AnticheatCheck> getAllChecks();
	
}
