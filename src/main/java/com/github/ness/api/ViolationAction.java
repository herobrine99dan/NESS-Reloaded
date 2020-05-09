package com.github.ness.api;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

import com.github.ness.Violation;

/**
 * An action which is run when a player is detected for a cheat. <br>
 * Usage: <i>Extend this class</i> and add it using {@link NESSApi#addViolationAction(ViolationAction)}.
 * 
 * @author A248
 *
 */
public abstract class ViolationAction implements BiConsumer<Player, Violation> {

	private final boolean canRunAsync;
	
	/**
	 * Create the violation action, specifying whether it must run synchronously.
	 * 
	 * @param canRunAsync false if the action must run synchronously, true otherwise
	 */
	public ViolationAction(boolean canRunAsync) {
		this.canRunAsync = canRunAsync;
	}
	
	/**
	 * Create the violation action. It will run on the main thread.
	 * 
	 */
	public ViolationAction() {
		this(false);
	}
	
	/**
	 * Whether the action can be run asynchronously. <br>
	 * If false, the action WILL be run on the main thread.
	 * 
	 * @return whether the action may run async
	 */
	public boolean canRunAsync() {
		return canRunAsync;
	}
	
}
