package com.github.ness.check;

import java.time.Duration;
import java.util.Objects;

/**
 * Determines how a check should be orchestrated
 *
 * @param <T> ignored parameter, kept for legacy purposes
 * 
 * @author A248
 */
public class CheckInfo extends BaseCheckInfo {

	/**
	 * Interval of repeating async task, {@link Duration#ZERO} for none
	 */
	private final Duration asyncInterval;

	CheckInfo(Duration asyncInterval) {
		if (asyncInterval.isNegative() || asyncInterval.isZero()) {
			throw new IllegalArgumentException("asyncInterval must be positive");
		}
		this.asyncInterval = Objects.requireNonNull(asyncInterval, "asyncInterval");
	}
	
	CheckInfo() {
		asyncInterval = Duration.ZERO;
	}
	
	boolean hasAsyncInterval() {
		return !asyncInterval.isZero();
	}
	
	Duration getAsyncInterval() {
		if (!hasAsyncInterval()) {
			throw new IllegalStateException("Cannot get async interval if there is none");
		}
		return asyncInterval;
	}

}
