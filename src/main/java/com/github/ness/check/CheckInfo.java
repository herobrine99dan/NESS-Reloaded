package com.github.ness.check;

import org.bukkit.event.Event;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Determines how a check should be orchestrated
 *
 * @param <T> ignored parameter, kept for legacy purposes
 * 
 * @author A248
 */
public class CheckInfo<T> extends BaseCheckInfo {

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
	
	@SuppressWarnings("unchecked")
	private static <E extends Event> CheckInfo<E> parameteriseForLegacy(ListeningCheckInfo<E> listeningCheckInfo) {
		CheckInfo<?> info = listeningCheckInfo;
		// Safe because the type parameter in CheckInfo is useless
		return (CheckInfo<E>) info;
	}
}
