package com.github.ness.check;

import java.util.concurrent.ScheduledFuture;

import org.bukkit.event.Event;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public abstract class AbstractCheck<T extends Event> {

	final CheckManager manager;
	private final CheckInfo<T> info;
	
	private ScheduledFuture<?> asyncFuture;
	
	AbstractCheck(CheckManager manager, CheckInfo<T> info) {
		this.manager = manager;
		this.info = info;
	}
	
	public void initiatePeriodicTasks() {
		if (info.asyncInterval != -1L) {
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
