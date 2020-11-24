package com.github.ness.check;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.packets.ReceivedPacketEvent;

import lombok.Getter;

/**
 * A check, associated with a player. Includes an optional async task.
 * 
 * @author A248
 *
 */
public abstract class Check {

    private final NessPlayer nessPlayer;

    private final AtomicInteger violations = new AtomicInteger();
    @Getter
    private final String checkName;
    private final Class<?> checkClass;
    private static CheckManager manager;

    protected Check(Class<?> classe, NessPlayer nessPlayer, CheckManager manager) {
        this.checkName = classe.getSimpleName();
        this.checkClass = classe;
        this.nessPlayer = nessPlayer;
        if (manager != null) {
            Check.manager = manager;
        }
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
    public final void flag(NessPlayer sender) {
        flag("", sender);
    }

    public abstract void checkEvent(ReceivedPacketEvent e);

    /**
     * Flags the player for cheating
     * 
     * @param details debugging details
     */
    public final void flag(String details, NessPlayer sender) {
        if (callViolationEvent()) {
            nessPlayer.getBukkitPlayer()
                    .sendMessage("Cheats Detected: " + this.getCheckName() + " Details: " + details + " Name: " + this.nessPlayer.getBukkitPlayer().getName());
        }
    }

    private boolean callViolationEvent() {
        if (com.github.ness.api.impl.PlayerViolationEvent.getHandlerList().getRegisteredListeners().length == 0) {
            return true;
        }
        return callEvent(new com.github.ness.api.impl.PlayerViolationEvent(nessPlayer.getBukkitPlayer(), nessPlayer,
                new com.github.ness.api.Violation(checkName, ""), violations.get()));
    }

    private boolean callEvent(Cancellable event) {
        Bukkit.getServer().getPluginManager().callEvent((Event) event);
        return !event.isCancelled();
    }

    public Check makeEqualCheck(NessPlayer nessPlayer) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        return (Check) this.checkClass.getConstructors()[0].newInstance(nessPlayer, this.manager());
    }

    public int currentViolationCount() {
        return violations.get();
    }

    public void clearViolationCount() {
        violations.set(0);
    }
}
