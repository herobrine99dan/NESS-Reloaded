package com.github.ness.check;

import java.time.Duration;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;

import org.bukkit.event.Event;

/**
 * General check to be extended. <br>
 * <br>
 * Subclasses must declare a public constructor with BaseCheckFactory as a
 * parameter, e.g.: <br>
 * <code>public XXXCheck(BaseCheckFactory<?> checkFactory)</code>
 *
 * @param <E> the type of the event listened to
 * @author A248
 */
public abstract class AbstractCheck<E extends Event> {

	private final CheckFactory<?> checkFactory;
	private final NessPlayer nessPlayer;

	/**
	 * Creates the check. Subclasses should declare a constructor with the same signature
	 *
	 * @param manager the check manager
	 * @param nessPlayer the ness player
	 */
	protected AbstractCheck(CheckFactory<?> checkFactory, NessPlayer nessPlayer) {
		this.checkFactory = checkFactory;
		this.nessPlayer = nessPlayer;
	}
	
	CheckFactory<?> getFactory() {
		return checkFactory;
	}

	protected CheckManager manager() {
		return checkFactory.getCheckManager();
	}
	
	protected NESSAnticheat ness() {
		return this.manager().getNess();
	}
	
	protected NessPlayer player() {
		return nessPlayer;
	}
	
	/**
	 * Runs a delayed task using the bukkit scheduler
	 * 
	 * @param command the runnable to run later
	 * @param duration the delay
	 */
	protected void runTaskLater(Runnable command, Duration duration) {
		manager().getNess().getServer().getScheduler().runTaskLater(manager().getNess(), command, duration.toMillis() / 50L);
	}
	
	protected Duration durationOfTicks(int ticks) {
		return Duration.ofMillis(ticks * 50L);
	}

	/**
	 * Called async and periodically, as defined by {@link CheckInfo}
	 *
	 */
	protected void checkAsyncPeriodic() {
		throw new UnsupportedOperationException("Not implemented - checkAsyncPeriodic");
	}

	/**
	 * Called when this check's event is fired, whether synchronously depends on
	 * whether the event is fired synchronously
	 *
	 * @param evt        the event
	 */
	protected void checkEvent(E evt) {
		throw new UnsupportedOperationException("Not implemented - checkEvent");
	}

}
