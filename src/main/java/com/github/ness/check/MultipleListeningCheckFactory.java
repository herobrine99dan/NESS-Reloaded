package com.github.ness.check;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MultipleListeningCheckFactory<C extends MultipleListeningCheck> extends CheckFactory<C> {
	
	private RegisteredMultipleListener registeredListener;

	protected MultipleListeningCheckFactory(CheckInstantiator<C> instantiator, String checkName, CheckManager manager,
			CheckInfo checkInfo) {
		super(instantiator, checkName, manager, checkInfo);
		registeredListener = new RegisteredMultipleListener(manager, this);
	}
	
	void checkEvent(Event event) {
		getChecksMap().values().forEach((check) -> {
			check.checkEventUnlessInvalid(event);
		});
	}
	
	@Override
	protected synchronized void start() {
		for(HandlerList handler : HandlerList.getHandlerLists()) {
		    handler.register(registeredListener);
		}
		super.start();
	}
	
	@Override
	protected synchronized void close() {
		for(HandlerList handler : HandlerList.getHandlerLists()) {
		    handler.unregister(registeredListener);
		}
		super.close();
	}

}
