package com.github.ness.check;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.api.impl.PlayerViolationEvent;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.NessEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;
import com.github.ness.packets.event.bukkit.NessBukkitEvent;

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

    protected Check(Class<?> classe, NessPlayer nessPlayer, boolean hasScheduler, long milliSeconds) {
        this.checkName = classe.getSimpleName();
        this.nessPlayer = nessPlayer;
        this.hasAsyncScheduler = hasScheduler;
        this.milliSeconds = milliSeconds;
    }

    /**
     * This method enables the schedule and will be called by CheckFactory
     */
    public void startScheduler() {
        if (hasAsyncScheduler) {
            manager().getNess().getExecutor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Check.this.checkAsyncPeriodic();
                }
            }, 1L, this.milliSeconds, TimeUnit.MILLISECONDS);
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

    public static void updateCheckManager(CheckManager manager) {
        Check.manager = manager;
    }

    public CheckManager manager() {
        return manager;
    }

    NessAnticheat ness() {
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
    public final int flag(NessEvent e) {
        return flag("", e);
    }

    /**
     * Flags the player for cheating
     * 
     */
    public final int flag(String details) {
        return flag(details, null);
    }

    public void onFlying(FlyingEvent e) {
    }

    public void onUseEntity(UseEntityEvent e) {
    }

    public void onEveryPacket(ReceivedPacketEvent e) {
    }

    public void onBukkitEvent(NessBukkitEvent e) {
    }
    
    /**
     * Flags the player for cheating
     * 
     */
    public final int flag(NessEvent e, String details) {
        return flag(details, e);
    }

    /**
     * Flags the player for cheating
     * 
     * @param details debugging details
     */
    public final int flag(String details, NessEvent e) {
        Violation violation = new Violation(this.checkName, details, this.violations.addAndGet(1));
        if (callViolationEvent(violation)) {
            this.nessPlayer.addViolation(violation);
            ness().getViolationHandler().onCheat(this.player().getBukkitPlayer(), violation, this);
            if (e == null) {
                return this.violations.get();
            }
            return this.violations.get();
        } else {
            this.violations.decrementAndGet();
        }
        return this.violations.get();
    }

    private boolean callViolationEvent(Violation violation) {
        if (PlayerViolationEvent.getHandlerList().getRegisteredListeners().length == 0) {
            return true;
        }
        PlayerViolationEvent event = new PlayerViolationEvent(nessPlayer.getBukkitPlayer(), nessPlayer, violation,
                violations.get());
        Bukkit.getServer().getPluginManager().callEvent((Event) event);
        return !event.isCancelled();
    }

    public int currentViolationCount() {
        return violations.get();
    }

    public void clearViolationCount() {
        violations.set(0);
    }

    /**
     * Runs a delayed task using the bukkit scheduler
     * 
     * @param command  the runnable to run later
     * @param duration the delay
     */
    public void runTaskLater(Runnable command, Duration duration) {
        JavaPlugin plugin = manager().getNess().getPlugin();
        plugin.getServer().getScheduler().runTaskLater(plugin, command, duration.toMillis() / 50L);
    }

    public Duration durationOfTicks(int ticks) {
        return Duration.ofMillis(ticks * 50L);
    }

    @Override
    public String toString() {
        return "Check [nessPlayer=" + nessPlayer.getBukkitPlayer().getName() + ", violations=" + violations
                + ", checkName=" + checkName + ", getCheckName()=" + getCheckName() + "]";
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
