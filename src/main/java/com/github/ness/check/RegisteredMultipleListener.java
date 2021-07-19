package com.github.ness.check;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;

class RegisteredMultipleListener extends RegisteredListener {

	private final MultipleListeningCheckFactory<?> checkFactory;

	RegisteredMultipleListener(CheckManager manager, MultipleListeningCheckFactory<?> checkFactory) {
		super(ScalableRegisteredListener.DummyListener.INSTANCE, ScalableRegisteredListener.DummyEventExecutor.INSTANCE,
				EventPriority.LOW, manager.getNess().getPlugin(), false);
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
}
