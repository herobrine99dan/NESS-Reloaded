package com.github.ness.check;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.Event;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public abstract class AbstractCheck<T extends Event> {

	final CheckManager manager;
	private final CheckInfo<T> info;
	
	private ScheduledFuture<?> asyncFuture;
	
	volatile boolean hasViolated;
	
	AbstractCheck(CheckManager manager, CheckInfo<T> info) {
		this.manager = manager;
		this.info = info;
	}
	
	public void initiatePeriodicTasks() {
		if (info.asyncInterval != -1L) {
			asyncFuture = manager.getNess().getExecutor().scheduleWithFixedDelay(() -> {
				manager.forEachPlayer(this::checkAsyncPeriodic);
			}, 1L, info.asyncInterval, TimeUnit.MILLISECONDS);
		}
	}
	
	public void close() {
		if (asyncFuture != null) {
			asyncFuture.cancel(false);
		}
	}
	
	public boolean hasViolated() {
		return hasViolated;
	}
	
	@SuppressWarnings("unchecked")
	public void checkAnyEvent(Event evt) {
		if (info.event != null && info.getClass().isInstance(evt)) {
			checkEvent((T) evt);
		}
	}
	
	// To be overriden by subclasses
	
	/**
	 * Called async and periodically for each player, as defined by {@link CheckInfo}
	 * 
	 * @param player the ness player
	 */
	public void checkAsyncPeriodic(NessPlayer player) {
		
	}
	
	abstract void checkEvent(T evt);
	
}
