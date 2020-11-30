package com.github.ness.api;

import org.bukkit.entity.Player;

/**
 * An action which is run when a player is detected for a cheat. <br>
 * Usage: <i>Extend this class</i> and add it using
 * {@link NESSApi#addViolationAction(ViolationAction)}.
 * @author A248
 */
public interface ViolationAction {
    /**
     * Called when a player is detected for cheating. <br>
     * The total number of times the player has violated the check in question (from
     * <code>Violation.getCheck()</code>) is given as <i>violationCount</i>.
     *
     * @param player         the player
     * @param violation      the violation
     * @param violationCount the number of times the player has violated the
     *                       specific check
     */
    public abstract void actOn(Player player, Violation violation);

}
