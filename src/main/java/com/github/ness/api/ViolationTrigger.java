package com.github.ness.api;

import org.bukkit.entity.Player;

/**
 * An action which is run when a player is detected for a cheat. <br>
 * Usage: <i>Implement</i> and add it using {@link NESSApi#addViolationTrigger(ViolationTrigger)}.
 *
 * @author A248
 */
@FunctionalInterface
public interface ViolationTrigger {

	/**
	 * Determines the synchronisation context of this trigger. The returned value may be assumed constant
	 * and should therefore be so.
	 * 
	 * @return the synchronisation context
	 */
	default SynchronisationContext context() {
		return SynchronisationContext.FORCE_SYNC;
	}
	
	/**
	 * Called when a player is detected for cheating. <br>
	 * <br>
     * The total number of times the player has violated the check in question
     * (from <code>Violation.getCheck()</code>) is given as <i>violationCount</i>.
	 * 
	 * @param player the player
	 * @param violation the violation
	 * @param violationCount the amount of times this specific check has been flagged
	 */
	void actOn(Player player, Violation violation, int violationCount);
	
	/**
	 * The context in which a trigger may run
	 * 
	 * @author A248
	 *
	 */
	enum SynchronisationContext {
		
		/**
		 * The trigger must run asynchronously
		 * 
		 */
		FORCE_ASYNC,
		/**
		 * The trigger may run in any thread context
		 * 
		 */
		EITHER,
		/**
		 * The trigger must run synchronously to the main thread
		 * 
		 */
		FORCE_SYNC
		
	}
	
}
