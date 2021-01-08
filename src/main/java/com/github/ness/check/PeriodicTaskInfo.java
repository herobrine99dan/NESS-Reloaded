package com.github.ness.check;

import java.time.Duration;

public final class PeriodicTaskInfo {

	private final Duration syncInterval;
	private final Duration asyncInterval;

	public PeriodicTaskInfo(Duration syncInterval, Duration asyncInterval) {
		this.syncInterval = syncInterval;
		this.asyncInterval = asyncInterval;
	}

	Duration syncInterval() {
		return syncInterval;
	}

	Duration asyncInterval() {
		return asyncInterval;
	}

	/*
	 * Factory methods
	 */

	private static final PeriodicTaskInfo NONE = new PeriodicTaskInfo(null, null);

	public static PeriodicTaskInfo none() {
		return NONE;
	}

	/**
	 * Specifes a <b>synchronous</b> repeating task
	 *
	 * @param interval the interval of execution
	 * @return the task info
	 */
	public static PeriodicTaskInfo syncTask(Duration interval) {
		if (interval.isNegative() || interval.isZero()) {
			throw new IllegalArgumentException("interval must be positive");
		}
		return new PeriodicTaskInfo(interval, null);
	}

	/**
	 * Specifes an <b>asynchronous</b> repeating task
	 *
	 * @param interval the interval of execution
	 * @return the task info
	 */
	public static PeriodicTaskInfo asyncTask(Duration interval) {
		if (interval.isNegative() || interval.isZero()) {
			throw new IllegalArgumentException("interval must be positive");
		}
		return new PeriodicTaskInfo(null, interval);
	}
}
