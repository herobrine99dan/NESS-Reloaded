package com.github.ness.check;

import org.bukkit.event.Event;

import java.util.Objects;

public class MultipleListeningCheckInfo extends CheckInfo {

	/**
	 * Events to listen to
	 */
	private final Class<? extends Event>[] events;

	MultipleListeningCheckInfo(Class<? extends Event>... events) {
		this.events = Objects.requireNonNull(events, "events");
	}

	MultipleListeningCheckInfo(PeriodicTaskInfo taskInfo, Class<? extends Event>... events) {
		super(taskInfo);
		this.events = Objects.requireNonNull(events, "events");
	}

	Class<? extends Event>[] getEvents() {
		return events;
	}
	
}

