package com.github.ness.check;

import java.util.concurrent.ScheduledFuture;

import org.bukkit.event.Event;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

/**
 * General check to be extended. <br>
 * <br>
 * Subclasses must declare a public constructor with CheckManager as a parameter, e.g.: <br>
 * <code>public XXXCheck(CheckManager manager)</code>
 * 
 * 
 * @author A248
 *
 * @param <T> the type of the event listened to
 */
public abstract class AbstractCheck<T extends Event> {

	final CheckManager manager;
	private final CheckInfo<T> info;
	
	private ScheduledFuture<?> asyncFuture;
	
	/**
	 * Creates the check. Subclasses should require CheckManager as a parameter in their constructors
	 * and simply pass it along to this superconstructor. They should make their own CheckInfo.
	 * 
	 * @param manager the check manager
	 * @param info information about the check, designated by the check itself
	 */
	AbstractCheck(CheckManager manager, CheckInfo<T> info) {
		this.manager = manager;
		this.info = info;
	}
	
	// To be called by CheckManager
	
	public void initiatePeriodicTasks() {
		if (info.asyncInterval != -1L) {
			assert asyncFuture == null;

			asyncFuture = manager.getNess().getExecutor().scheduleWithFixedDelay(() -> {
				manager.forEachPlayer(this::checkAsyncPeriodic);
			}, 1L, info.asyncInterval, info.units);
		}
	}
	
	public void close() {
		if (asyncFuture != null) {
			asyncFuture.cancel(false);
		}
	}
	
	public void checkAnyEvent(Event evt) {
		if (info.event != null && info.event.isInstance(evt)) {
			checkEvent(info.event.cast(evt));
		}
	}
	
	// To be overriden by subclasses
	
	/**
	 * Called async and periodically for each player, as defined by {@link CheckInfo}
	 * 
	 * @param player the ness player
	 */
	void checkAsyncPeriodic(NessPlayer player) {
		
	}
	
	/**
	 * Called synchronously when this check's event is fired
	 * 
	 * @param evt the event
	 */
	void checkEvent(T evt) {
		
	}
	
}
