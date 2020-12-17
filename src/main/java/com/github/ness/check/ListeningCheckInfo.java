package com.github.ness.check;

import org.bukkit.event.Event;

import java.util.Objects;

/**
 * Check info relating to listening of events
 * 
 * @author A248
 *
 * @param <E>
 */
public class ListeningCheckInfo<E extends Event> extends CheckInfo {

	/**
	 * Event to listen to
	 */
	private final Class<E> event;

	ListeningCheckInfo(Class<E> event) {
		this.event = Objects.requireNonNull(event, "event");
	}

	ListeningCheckInfo(PeriodicTaskInfo taskInfo, Class<E> event) {
		super(taskInfo);
		this.event = Objects.requireNonNull(event, "event");
	}

	Class<E> getEvent() {
		return event;
	}
	
}
