package com.github.ness.check;

import org.bukkit.event.Event;

public class MultipleListeningCheckFactory<C extends MultipleListeningCheck> extends CheckFactory<C> {

	private final RegisteredMultipleListener registeredListener;

	private final Class<? extends Event>[] events;

	protected MultipleListeningCheckFactory(CheckInstantiator<C> instantiator, String checkName, CheckManager manager,
			MultipleListeningCheckInfo checkInfo) {
		super(instantiator, checkName, manager, checkInfo);
		registeredListener = new RegisteredMultipleListener(manager, this);
		events = checkInfo.getEvents();
	}

	void checkEvent(Event event) {
		getChecksMap().values().forEach((check) -> {
			check.checkEventUnlessInvalid(event);
		});
	}

	@Override
	protected synchronized void start() {
		for (Class<? extends Event> classe : events) {
			new HandlerListSearch<>(classe).search().register(registeredListener);
		}
		super.start();
	}

	@Override
	protected synchronized void close() {
		for (Class<? extends Event> classe : events) {
			new HandlerListSearch<>(classe).search().unregister(registeredListener);
		}
		super.close();
	}

}
