package com.github.ness.check;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.api.impl.PlayerViolationEvent;
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
    private static CheckManager manager;
    private final boolean hasAsyncScheduler;
    private final long milliSeconds;

    protected Check(Class<?> classe, NessPlayer nessPlayer) {
        this.checkName = classe.getSimpleName();
        this.nessPlayer = nessPlayer;
        this.hasAsyncScheduler = false;
        this.milliSeconds = 0L;
    }
    
    protected Check(Class<?> classe, NessPlayer nessPlayer, boolean scheduler, Duration d) {
        this.checkName = classe.getSimpleName();
        this.nessPlayer = nessPlayer;
        this.hasAsyncScheduler = scheduler;
        
        this.milliSeconds = d.toMillis();
    }

    /**
     * Gets the player this check is for
     * 
     * @return the player analysed by this check
     */
    protected NessPlayer player() {
        return nessPlayer;
    }
    
    public static void updateCheckManager(CheckManager manager) {
        Check.manager = manager;
    }

    protected CheckManager manager() {
        return manager;
    }

    protected NessAnticheat ness() {
        return manager.getNess();
    }

    /**
     * Called async and periodically
     *
     */
    public void checkAsyncPeriodic() {
        throw new UnsupportedOperationException("Not implemented - checkAsyncPeriodic");
    }

    /**
     * Flags the player for cheating
     * 
     */
    public final void flag() {
        flag("");
    }

    public abstract void checkEvent(ReceivedPacketEvent e);

    /**
     * Flags the player for cheating
     * 
     * @param details debugging details
     */
    public final int flag(String details) {
        if (callViolationEvent()) {
            nessPlayer.getBukkitPlayer()
                    .sendMessage("Cheats Detected: " + this.getCheckName() + " Details: " + details + " Name: " + this.nessPlayer.getBukkitPlayer().getName());
            return this.violations.addAndGet(1);
        }
        return this.violations.get();
    }

    private boolean callViolationEvent() {
        if (PlayerViolationEvent.getHandlerList().getRegisteredListeners().length == 0) {
            return true;
        }
        return callEvent(new PlayerViolationEvent(nessPlayer.getBukkitPlayer(), nessPlayer,
                new Violation(checkName, ""), violations.get()));
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
    
    @Override
    public String toString() {
        return "Check [nessPlayer=" + nessPlayer.getBukkitPlayer().getName() + ", violations=" + violations + ", checkName=" + checkName
                + ", getCheckName()=" + getCheckName() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((checkName == null) ? 0 : checkName.hashCode());
        result = prime * result + ((nessPlayer == null) ? 0 : nessPlayer.hashCode());
        result = prime * result + ((violations == null) ? 0 : violations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Check))
            return false;
        Check other = (Check) obj;
        if (checkName == null) {
            if (other.checkName != null)
                return false;
        } else if (!checkName.equals(other.checkName))
            return false;
        if (nessPlayer == null) {
            if (other.nessPlayer != null)
                return false;
        } else if (!nessPlayer.equals(other.nessPlayer))
            return false;
        if (violations == null) {
            if (other.violations != null)
                return false;
        } else if (!violations.equals(other.violations))
            return false;
        return true;
    }
}
