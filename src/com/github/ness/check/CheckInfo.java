package com.github.ness.check;

import org.bukkit.event.Event;

public class CheckInfo<T extends Event> {

	final long asyncInterval;
	
	final Class<T> event;
	
	private CheckInfo(long asyncInterval, Class<T> event) {
		this.asyncInterval = asyncInterval;
		this.event = event;
	}
	
	static <T extends Event> CheckInfo<T> eventWithAsyncPeriodic(long intervalTicks, Class<T> event) {
		return new CheckInfo<>(intervalTicks, event);
	}
	
	static <T extends Event> CheckInfo<T> eventOnly(Class<T> event) {
		return new CheckInfo<>(-1L, event);
	}
	
}
