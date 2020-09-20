package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.ness.NessPlayer;

public class CheckFactory<C extends AbstractCheck<?>> {

	private final CheckInstantiator<C> instantiator;
	private final String checkName;
	private final CheckManager manager;
	private final CheckInfo<?> checkInfo;
	
	private transient boolean started;
	private transient ScheduledFuture<?> scheduledFuture;
	
	private transient final ConcurrentMap<UUID, C> checks = new ConcurrentHashMap<>();
	
	CheckFactory(Constructor<C> constructor, CheckManager manager, CheckInfo<?> checkInfo) {
		this(CheckInstantiators.fromConstructor(constructor),
				constructor.getDeclaringClass().getSimpleName().toLowerCase(Locale.ROOT),
				manager, checkInfo);
	}
	
	protected CheckFactory(CheckInstantiator<C> instantiator, String checkName, CheckManager manager, CheckInfo<?> checkInfo) {
		this.instantiator = instantiator;
		this.checkName = checkName;
		this.manager = manager;
		this.checkInfo = checkInfo;
	}
	
	CheckManager getCheckManager() {
		return manager;
	}
	
	String getCheckName() {
		return checkName;
	}
	
	Map<UUID, C> getChecksMap() {
		return checks;
	}
	
	private void checkAsyncPeriodic() {
		synchronized (this) {
			if (!started) {
				return;
			}
		}
		checks.values().forEach((check) -> check.checkAsyncPeriodic());
	}
	
	C newCheck(NessPlayer nessPlayer) {
		C check = instantiator.newCheck(this, nessPlayer);
		checks.put(nessPlayer.getUUID(), check);
		return check;
	}
	
	void removeCheck(NessPlayer nessPlayer) {
		checks.remove(nessPlayer.getUUID());
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

		if (checkInfo.hasAsyncInterval()) {
			Duration asyncInterval = checkInfo.getAsyncInterval();
			scheduledFuture = manager.getNess().getExecutor().scheduleWithFixedDelay(
					this::checkAsyncPeriodic, 0L, asyncInterval.toNanos(), TimeUnit.NANOSECONDS);

		} else {
			scheduledFuture = null;
		}
	}
	
	final void close() {
		synchronized (this) {
			close0();
		}
	}
	
	void close0() {
		assert Thread.holdsLock(this);

		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
		}
	}

	@Override
	public String toString() {
		return "CheckFactory [instantiator=" + instantiator + ", manager=" + manager + ", checkInfo=" + checkInfo
				+ ", checkName=" + checkName + "]";
	}
	
}
