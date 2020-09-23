package com.github.ness.api;

import org.bukkit.entity.Player;

/**
 * An action which is run when a player is detected for a cheat. <br>
 * Usage: <i>Extend this class</i> and add it using {@link NESSApi#addViolationAction(ViolationAction)}.
 *
 * @author A248
 * @deprecated It is preferred to use {@link ViolationTrigger}, which is an interface, and has finer control
 * over specifying the synchronisation context of the trigger
 */
@Deprecated
public abstract class ViolationAction implements ViolationTrigger {

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
    
    @Override
	public SynchronisationContext context() {
    	return (canRunAsync) ? SynchronisationContext.FORCE_ASYNC : SynchronisationContext.FORCE_SYNC;
    }

    /**
     * Called when a player is detected for cheating. <br>
     * The total number of times the player has violated the check in question
     * (from <code>Violation.getCheck()</code>) is given as <i>violationCount</i>.
     *
     * @param player         the player
     * @param violation      the violation
     * @param violationCount the number of times the player has violated the specific check
     */
    @Override
	public abstract void actOn(Player player, Violation violation, int violationCount);

}
