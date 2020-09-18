package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;

import com.github.ness.NessPlayer;
import com.github.ness.utility.UncheckedReflectiveOperationException;

public class CheckFactory<C extends AbstractCheck<?>> {

	private final Constructor<C> constructor;
	private final CheckManager manager;
	private final CheckInfo<?> checkInfo;
	
	private boolean started;
	private ScheduledFuture<?> scheduledFuture;
	
	private final Set<C> checks = new CopyOnWriteArraySet<>();
	
	CheckFactory(Constructor<C> constructor, CheckManager manager, CheckInfo<?> checkInfo) {
		this.constructor = constructor;
		this.manager = manager;
		this.checkInfo = checkInfo;
	}
	
	CheckManager getCheckManager() {
		return manager;
	}
	
	String getCheckName() {
		return constructor.getDeclaringClass().getSimpleName().toLowerCase(Locale.ROOT);
	}
	
	Set<C> getChecks() {
		return checks;
	}
	
	private void checkAsyncPeriodic() {
		synchronized (this) {
			if (!started) {
				return;
			}
		}
		for (C check : checks) {
			check.checkAsyncPeriodic();
		}
	}
	
	C newCheck(NessPlayer nessPlayer) {
		C check;
		try {
			check = constructor.newInstance(manager, nessPlayer);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
			throw new UncheckedReflectiveOperationException(
					"Unable to instantiate check " + constructor.getDeclaringClass().getName(), ex);
		}
		checks.add(check);
		return check;
	}
	
	/**
	 * Called to start the check
	 * 
	 */
	final void start() {
		synchronized (this) {
			if (!started) {
				start0();
			}
			started = true;
		}
		
	}
	
	void start0() {
		assert Thread.holdsLock(this);

		if (checkInfo.asyncInterval == -1) {
			scheduledFuture = null;
		} else {
			scheduledFuture = manager.getNess().getExecutor().scheduleWithFixedDelay(
					this::checkAsyncPeriodic, 0L, checkInfo.asyncInterval, checkInfo.units);
		}
	}
	
	final void close() {
		synchronized (this) {
			close0();
		}
	}
	
	void close0() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
		}
	}
	
}
