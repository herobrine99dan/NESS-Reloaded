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
	 * Gets a check info with the given periodic task info
	 *
	 * @param taskInfo the periodic task info
	 * @return the check info
	 */
	public static CheckInfo withTask(PeriodicTaskInfo taskInfo) {
		return new CheckInfo(taskInfo);
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

	/*
	 * There is currently no separate CheckInfo for packet checks, so in implementation,
	 * the following are equivalent to #withTask
	 */

	/**
	 * Gets a check info to be used with packet checks
	 *
	 * @return a check info for packet checks
	 */
	public static CheckInfo forPackets() {
		return withTask(PeriodicTaskInfo.none());
	}
	
	/**
	 * Gets a check info to be used with multiple-events checks
	 *
	 * @return a check info for multiple-events checks
	 */
	public static MultipleListeningCheckInfo forMultipleEventListener(Class<? extends Event>... array) {
		return new MultipleListeningCheckInfo(PeriodicTaskInfo.none(), array);
	}

	/**
	 * Gets a check info to be used with packet checks, plus a periodic sync or async task.
	 *
	 * @param taskInfo the periodic task info
	 * @return a check info for packet checks
	 */
	public static CheckInfo forPacketsWithTask(PeriodicTaskInfo taskInfo, Class<? extends Event>... array) {
		return withTask(taskInfo);
	}
	
	/**
	 * Gets a check info to be used with multiple-event checks, plus a periodic sync or async task.
	 *
	 * @param taskInfo the periodic task info
	 * @return a check info for multple-events checks
	 */
	public static MultipleListeningCheckInfo forMultipleEventListenerWithTask(PeriodicTaskInfo taskInfo, Class<? extends Event>... array) {
		return new MultipleListeningCheckInfo(PeriodicTaskInfo.none(), array);
	}
}
