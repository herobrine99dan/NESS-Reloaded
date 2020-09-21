package com.github.ness.check;

import com.github.ness.NessPlayer;

import org.bukkit.event.Event;

/**
 * Legacy check class
 *
 * @param <E> the type of the event listened to
 * @author A248
 * @deprecated Use one of {@link BaseCheck}, {@link Check}, or {@link ListeningCheck} depending on what this check does
 */
@Deprecated
public abstract class AbstractCheck<E extends Event> extends ListeningCheck<E> {

	/**
	 * Creates the check. Subclasses should declare a constructor with the same signature
	 *
	 * @param factory the check factory
	 * @param nessPlayer the ness player
	 */
	@SuppressWarnings("unchecked")
	protected AbstractCheck(CheckFactory<?> factory, NessPlayer nessPlayer) {
		super((ListeningCheckFactory<?, E>) factory, nessPlayer);
	}

	/**
	 * Called async and periodically, as defined by {@link CheckInfo}
	 *
	 */
	@Override
	protected void checkAsyncPeriodic() {
		throw new UnsupportedOperationException("Not implemented - checkAsyncPeriodic");
	}

	/**
	 * Called when this check's event is fired, whether synchronously depends on
	 * whether the event is fired synchronously
	 *
	 * @param evt        the event
	 */
	@Override
	protected void checkEvent(E evt) {
		throw new UnsupportedOperationException("Not implemented - checkEvent");
	}

}
