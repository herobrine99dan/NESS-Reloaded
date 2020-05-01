package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.event.Event;

/**
 * Relates the type of the check, specifically when it occurs,
 * whether on an event, periodically, etc.
 * 
 * @author A248
 *
 * @param <T> the type of the event
 */
public class CheckInfo<T extends Event> {

	final long asyncInterval;
	final TimeUnit units;
	
	final Class<T> event;
	
	private CheckInfo(Class<T> event, long asyncInterval, TimeUnit units) {
		this.event = event;
		this.asyncInterval = asyncInterval;
		this.units = units;
	}
	
	static <T extends Event> CheckInfo<T> eventWithAsyncPeriodic(Class<T> event, long interval, TimeUnit units) {
		return new CheckInfo<>(event, interval, units);
	}
	
	static <T extends Event> CheckInfo<T> eventOnly(Class<T> event) {
		return new CheckInfo<>(event, -1L, null);
	}
	
}
