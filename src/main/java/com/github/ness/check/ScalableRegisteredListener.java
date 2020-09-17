package com.github.ness.check;

import com.github.ness.utility.HandlerListUtils;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;

class ScalableRegisteredListener<E extends Event> extends RegisteredListener {
	
	private final ListeningCheckFactory<E, ?> checkFactory;
	
	ScalableRegisteredListener(CheckManager manager, ListeningCheckFactory<E, ?> checkFactory) {
		super(HandlerListUtils.DummyListener.INSTANCE, HandlerListUtils.DummyEventExecutor.INSTANCE,
				EventPriority.LOW, manager.getNess(), false);
		this.checkFactory = checkFactory;
	}
	
	@Override
	public void callEvent(final Event event) throws EventException {
		try {
			Class<E> eventClass = checkFactory.getEventClass();
			if (eventClass.isInstance(event)) {
				checkFactory.checkEvent(eventClass.cast(event));
			}
		} catch (Throwable ex) {
			// No one likes catching Throwable, but this is per Bukkit
			throw new EventException(
					ex, "NESS made a mistake in listening to an event. Please report this error on Github.");
		}
	}
	
}
