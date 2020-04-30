package com.github.ness.check;

import org.bukkit.event.Event;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;

public abstract class AbstractCheck<T extends Event> {

	final CheckManager manager;
	private final CheckInfo<T> info;
	
	volatile boolean hasViolated;
	
	private long lastAsyncPeriodicCheck;
	
	AbstractCheck(CheckManager manager, CheckInfo<T> info) {
		this.manager = manager;
		this.info = info;
		lastAsyncPeriodicCheck = (info.asyncInterval != -1L) ? System.currentTimeMillis() : -1L;
	}
	
	public boolean hasViolated() {
		return hasViolated;
	}
	
	public boolean canCheckAsyncPeriodic() {
		if (lastAsyncPeriodicCheck != -1L) {
			long now = System.currentTimeMillis();
			if (now - lastAsyncPeriodicCheck > info.asyncInterval) {
				lastAsyncPeriodicCheck = now;
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void checkAnyEvent(Event evt) {
		if (info.event != null && info.getClass().isInstance(evt)) {
			checkEvent((T) evt);
		}
	}
	
	// To be overriden by subclasses
	
	public void checkAsyncPeriodic(NessPlayer player) {
		
	}
	
	abstract void checkEvent(T evt);
	
}
