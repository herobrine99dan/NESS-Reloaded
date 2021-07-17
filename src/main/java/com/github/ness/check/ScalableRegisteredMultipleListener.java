package com.github.ness.check;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

class ScalableRegisteredMultipleListener extends RegisteredListener {

	private final MultipleListeningCheckFactory<?> checkFactory;

	ScalableRegisteredMultipleListener(CheckManager manager, MultipleListeningCheckFactory<?> checkFactory) {
		super(DummyListener.INSTANCE, DummyEventExecutor.INSTANCE, EventPriority.LOW, manager.getNess().getPlugin(),
				false);
		this.checkFactory = checkFactory;
	}

	@Override
	public void callEvent(final Event event) throws EventException {
		try {
			checkFactory.checkEvent(event);
		} catch (RuntimeException | AssertionError | IncompatibleClassChangeError ex) {
			// Catching so many unchecked exceptions is sub-optimal, but it is preferable to
			// avoid crashing the server
			throw new EventException(ex,
					"NESS made a mistake in listening to an event. Please report this error on Github.");
		}
	}

	/**
	 * Empty {@code Listener} for use in registered listeners to maintain
	 * compatibility with {@code RegisteredListener}'s undocumented nonnull
	 * contracts.
	 *
	 * @author A248
	 */
	private static final class DummyListener implements Listener {

		static final DummyListener INSTANCE = new DummyListener();

		private DummyListener() {
		}

	}

	/**
	 * Empty {@code EventExecutor} for use in registered listeners to maintain
	 * compatibility with {@code RegisteredListener}'s undocumented nonnull
	 * contracts.
	 *
	 * @author A248
	 */
	private static final class DummyEventExecutor implements EventExecutor {

		static final DummyEventExecutor INSTANCE = new DummyEventExecutor();

		private DummyEventExecutor() {
		}

		/**
		 * This method should never be called; {@code RegisteredListener}s utilising
		 * this dummy instance should override the {@code callEvent} method.
		 *
		 * @throws EventException always
		 */
		@Override
		public void execute(Listener listener, Event event) throws EventException {
			throw new EventException("DummyEventExecutor should never be invoked");
		}

	}

}
