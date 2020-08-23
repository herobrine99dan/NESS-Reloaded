package com.github.ness.check;

import java.util.concurrent.ScheduledFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.utility.HandlerListUtils;

/**
 * General check to be extended. <br>
 * <br>
 * Subclasses must declare a public constructor with CheckManager as a parameter, e.g.: <br>
 * <code>public XXXCheck(CheckManager manager)</code>
 * 
 * 
 * @author A248
 *
 * @param <E> the type of the event listened to
 */
public abstract class AbstractCheck<E extends Event> {

	final CheckManager manager;
	private final CheckInfo<E> info;
	
	private State state;
	private final Object stateLock = new Object();
	
	private static final Logger logger = LogManager.getLogger(AbstractCheck.class);
	
	/**
	 * Creates the check. Subclasses should require CheckManager as a parameter in their constructors
	 * and simply pass it along to this superconstructor. They should make their own CheckInfo.
	 * 
	 * @param manager the check manager
	 * @param info information about the check, designated by the check itself
	 */
	AbstractCheck(CheckManager manager, CheckInfo<E> info) {
		this.manager = manager;
		this.info = info;
	}
	
	private class State {
		
		final ScheduledFuture<?> asyncFuture;
		final NessRegisteredListener nessListener;
		
		State(ScheduledFuture<?> asyncFuture, NessRegisteredListener nessListener) {
			this.asyncFuture = asyncFuture;
			this.nessListener = nessListener;
		}
		
		void close() {
			if (asyncFuture != null) {
				asyncFuture.cancel(false);
			}
			if (nessListener != null) {
				HandlerListUtils.getEventListeners(info.event).unregister(nessListener);
			}
		}
		
	}
	
	private class NessRegisteredListener extends RegisteredListener {
		
		NessRegisteredListener() {
			super(HandlerListUtils.DummyListener.INSTANCE, HandlerListUtils.DummyEventExecutor.INSTANCE,
					EventPriority.NORMAL, manager.getNess(), false);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void callEvent(Event event) {
			try {
				if (info.event.isInstance(event)) {
					checkEvent((E) event);
				}
			} catch (Throwable ex) {
				logger.warn("NESS made a mistake in listening to an event. Please report this error on Github.", ex);
			}
		}
		
	}
	
	// To be called by CheckManager
	
	public void initiate() {
		synchronized (stateLock) {
			this.state = getNewState();
		}
	}
	
	// Should only be called under state lock
	private State getNewState() {
		assert Thread.holdsLock(stateLock);

		ScheduledFuture<?> asyncFuture = null;
		NessRegisteredListener nessListener = null;
		if (info.asyncInterval != -1L) {
			asyncFuture = manager.getNess().getExecutor().scheduleWithFixedDelay(() -> {
				manager.forEachPlayer(this::checkAsyncPeriodic);
			}, 1L, info.asyncInterval, info.units);
		}
		if (info.event != null) {
			nessListener = new NessRegisteredListener();
			HandlerListUtils.getEventListeners(info.event).register(nessListener);
		}
		return new State(asyncFuture, nessListener);
	}
	
	public void close() {
		synchronized (stateLock) {
			state.close();
			state = null;
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
	 * Called when this check's event is fired, whether synchronously depends on whether
	 * the event is fired synchronously
	 * 
	 * @param evt the event
	 */
	void checkEvent(E evt) {
		
	}
	
}
