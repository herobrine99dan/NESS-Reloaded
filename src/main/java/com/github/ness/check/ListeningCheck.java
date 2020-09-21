package com.github.ness.check;

import com.github.ness.NessPlayer;

import org.bukkit.event.Event;

/**
 * A check listening to a certain event
 * 
 * @author A248
 *
 * @param <E> the event type
 */
public abstract class ListeningCheck<E extends Event> extends Check {

	protected ListeningCheck(ListeningCheckFactory<?, E> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}
	
	@Override
	protected ListeningCheckFactory<?, E> getFactory() {
		@SuppressWarnings("unchecked")
		ListeningCheckFactory<?, E> factory = (ListeningCheckFactory<?, E>) super.getFactory();
		return factory;
	}
	
	/**
	 * Called when this check's event is fired, whether synchronously depends on
	 * whether the event is fired synchronously
	 *
	 * @param evt        the event
	 */
	protected abstract void checkEvent(E evt);

}
