package com.github.ness.check;

import java.time.Duration;

import org.bukkit.event.Event;

/**
 * Entry point for retrieving check info objects
 * 
 * @author A248
 *
 */
public final class CheckInfos {
	
	private CheckInfos() {}

	/**
	 * Gets a check info demanding a periodic async task
	 * 
	 * @param asyncInterval the interval of the periodic task
	 * @return the check info
	 * @deprecated Use {@link #withTask(PeriodicTaskInfo)}
	 */
	@Deprecated
	public static CheckInfo asyncPeriodic(Duration asyncInterval) {
		return withTask(PeriodicTaskInfo.asyncTask(asyncInterval));
	}

	/**
	 * Gets a check info with the given periodic task info
	 *
	 * @param taskInfo the periodic task info
	 * @return the check info
	 */
	public static CheckInfo withTask(PeriodicTaskInfo taskInfo) {
		return new CheckInfo(taskInfo);
	}

	/**
	 * Gets a check info to listen to a certain event <i>and</i> with a periodic
	 * async task
	 * 
	 * @param <E>           the event
	 * @param event         the event class
	 * @param asyncInterval the interval of the periodic task
	 * @return the listening check info
	 * @deprecated Use {@link #forEventWithTask(Class, PeriodicTaskInfo)}
	 */
	@Deprecated
	public static <E extends Event> ListeningCheckInfo<E> forEventWithAsyncPeriodic(Class<E> event,
			Duration asyncInterval) {
		return forEventWithTask(event, PeriodicTaskInfo.asyncTask(asyncInterval));
	}

	/**
	 * Gets a check info to listen to a certain event
	 * 
	 * @param <E>   the event
	 * @param event the event class
	 * @return the listening check info
	 */
	public static <E extends Event> ListeningCheckInfo<E> forEvent(Class<E> event) {
		return new ListeningCheckInfo<>(event);
	}

	/**
	 * Gets a check info to listen to a certain event, plus a periodic sync or async task.
	 *
	 * @param <E>   the event
	 * @param event the event class
	 * @param taskInfo the periodic task info
	 * @return the listening check info
	 */
	public static <E extends Event> ListeningCheckInfo<E> forEventWithTask(Class<E> event,
																		   PeriodicTaskInfo taskInfo) {
		return new ListeningCheckInfo<>(taskInfo, event);
	}
	
}
