package com.github.ness.check;

import java.lang.reflect.Constructor;

import com.github.ness.utility.HandlerListUtils;

import org.bukkit.event.Event;

class ListeningCheckFactory<E extends Event, C extends AbstractCheck<E>> extends BaseCheckFactory<C> {

	private final ScalableRegisteredListener<E> scalableListener;
	private final Class<E> eventClass;
	
	ListeningCheckFactory(Constructor<C> constructor, CheckManager manager, CheckInfo<E> checkInfo) {
		super(constructor, manager, checkInfo);
		scalableListener = new ScalableRegisteredListener<>(manager, this);
		eventClass = checkInfo.event;
	}
	
	Class<E> getEventClass() {
		return eventClass;
	}
	
	ScalableRegisteredListener<E> getScalableListener() {
		return scalableListener;
	}
	
	void checkEvent(E event) {
		getChecks().forEach((check) -> check.checkEvent(event));
	}
	
	@Override
	void close() {
		super.close();
		HandlerListUtils.getEventListeners(eventClass).unregister(getScalableListener());
	}

}
