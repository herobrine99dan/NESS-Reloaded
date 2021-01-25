package com.github.ness.check;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.PlayerFlagEvent;
import com.github.ness.violation.ViolationHandling;

/**
 * A check, associated with a player. Includes an optional async task.
 * 
 * @author A248
 *
 */
public class Check extends BaseCheck {
	
	private final NessPlayer nessPlayer;
	
	private final AtomicInteger violations = new AtomicInteger();
	
	protected Check(CheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory);
		this.nessPlayer = nessPlayer;
	}
	
	@Override
	public CheckFactory<?> getFactory() {
		return (CheckFactory<?>) super.getFactory();
	}
	
	public interface CheckConfig extends ViolationHandling {
		
	}
	
	/**
	 * Gets the player this check is for
	 * 
	 * @return the player analysed by this check
	 */
	protected NessPlayer player() {
		return nessPlayer;
	}
	
	/**
	 * Called async and periodically, if defined by {@link CheckInfo}
	 *
	 */
	protected void checkAsyncPeriodic() {
		throw new UnsupportedOperationException("Not implemented - checkAsyncPeriodic. Check: " + getClass().getName());
	}

	void checkAsyncPeriodicUnlessInvalid() {
		if (player().isInvalid()) {
			return;
		}
		checkAsyncPeriodic();
	}

	/**
	 * Called sync and periodically, if defined by {@link CheckInfo}
	 *
	 */
	protected void checkSyncPeriodic() {
		throw new UnsupportedOperationException("Not implemented - checkSyncPeriodic. Check: " + getClass().getName());
	}

	void checkSyncPeriodicUnlessInvalid() {
		if (player().isInvalid()) {
			return;
		}
		checkSyncPeriodic();
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
		JavaPlugin plugin = getFactory().getCheckManager().getNess().getPlugin();

		return callEvent(plugin, new PlayerFlagEvent(nessPlayer, getFactory()));
	}
	
	private boolean callEvent(JavaPlugin plugin, Cancellable event) {
		plugin.getServer().getPluginManager().callEvent((Event) event);
		return !event.isCancelled();
	}
	
	public int currentViolationCount() {
		return violations.get();
	}

	public void clearViolationCount() {
		violations.set(0);
	}

}
