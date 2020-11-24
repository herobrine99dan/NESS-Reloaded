package com.github.ness.check;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.PlayerFlagEvent;

/**
 * A check, associated with a player. Includes an optional async task.
 * 
 * @author A248
 *
 */
public class Check {

    private final NessPlayer nessPlayer;

    private final AtomicInteger violations = new AtomicInteger();
    private final String checkName;
    private final CheckManager manager;

    protected Check(String name, NessPlayer nessPlayer, CheckManager manager) {
        this.checkName = name;
        this.nessPlayer = nessPlayer;
        this.manager = manager;
    }

    /**
     * Gets the player this check is for
     * 
     * @return the player analysed by this check
     */
    protected NessPlayer player() {
        return nessPlayer;
    }

    protected CheckManager manager() {
        return manager;
    }

    protected NessAnticheat ness() {
        return manager.getNess();
    }

    /**
     * Called async and periodically, if defined by {@link CheckInfo}
     *
     */
    protected void checkAsyncPeriodic() {
        throw new UnsupportedOperationException("Not implemented - checkAsyncPeriodic");
    }

    /**
     * Flags the player for cheating
     * 
     */
    protected final void flag() {
        flag("");
    }

    /**
     * Flags the player for cheating
     * 
     * @param details debugging details
     */
    protected final void flag(String details) {
        if (callFlagEvent()) {
            flag0(details);
        }
    }

    /**
     * Flags and gets the infraction
     * 
     * @param details debugging details
     * @return the infraction
     */
    Infraction flag0(String details) {
        int violations = this.violations.incrementAndGet();
        Infraction infraction = new InfractionImpl(this, violations, details);
        nessPlayer.addInfraction(infraction);
        return infraction;
    }

    boolean callFlagEvent() {
        return callEvent(new PlayerFlagEvent(nessPlayer, getFactory()))
                && callDeprecatedPlayerViolationEvent();
    }

    @SuppressWarnings("deprecation")
    private boolean callDeprecatedPlayerViolationEvent() {
        if (com.github.ness.api.impl.PlayerViolationEvent.getHandlerList().getRegisteredListeners().length == 0) {
            return true;
        }
        return callEvent(new com.github.ness.api.impl.PlayerViolationEvent(nessPlayer.getBukkitPlayer(),
                nessPlayer, new com.github.ness.api.Violation(checkName, ""), violations.get()));
    }

    private boolean callEvent(Cancellable event) {
        Bukkit.getServer().getPluginManager().callEvent((Event) event);
        return !event.isCancelled();
    }

    public int currentViolationCount() {
        return violations.get();
    }

    public void clearViolationCount() {
        violations.set(0);
    }

}
