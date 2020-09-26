package com.github.ness.api;

/**
 * Manager of infraction triggers
 * 
 * @author A248
 *
 */
public interface InfractionManager {

	/**
	 * Adds an infraction trigger which is run when a player is detected for a cheat
	 * 
	 * @param trigger the trigger to add
	 * @throws NullPointerException if {@code trigger} is null
	 * @throws IllegalStateException if the specified trigger is already added
	 */
	void addTrigger(InfractionTrigger trigger);
	
	/**
	 * Removes a trigger which was added with {@link #addTrigger(InfractionTrigger)}. If the
	 * trigger was not added with such method, this returns {@code false}
	 * 
	 * @param trigger the trigger to remove
	 * @throws NullPointerException if {@code trigger} is null
	 * @return true if the trigger was removed, false otherwise
	 */
	boolean removeTrigger(InfractionTrigger trigger);
	
}
