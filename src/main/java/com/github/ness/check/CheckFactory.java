package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.ness.NessPlayer;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.FlagResult;
import com.github.ness.api.Infraction;

/**
 * Check factory for all per player checks
 * 
 * @author A248
 *
 * @param <C> the type of the check
 */
public class CheckFactory<C extends Check> extends BaseCheckFactory<C> {

	private final CheckInstantiator<C> instantiator;
	
	private transient ScheduledFuture<?> scheduledFuture;
	
	private transient final ConcurrentMap<UUID, C> checks = new ConcurrentHashMap<>();
	
	CheckFactory(Constructor<C> constructor, CheckManager manager, CheckInfo checkInfo) {
		this(CheckInstantiators.fromFactoryAndPlayerConstructor(constructor),
				constructor.getDeclaringClass().getSimpleName(),
				manager, checkInfo);
	}
	
	protected CheckFactory(CheckInstantiator<C> instantiator, String checkName, CheckManager manager, CheckInfo checkInfo) {
		super(checkName, manager, checkInfo);
		this.instantiator = instantiator;
	}
	
	@Override
	CheckInfo getCheckInfo() {
		return (CheckInfo) super.getCheckInfo();
	}
	
	Map<UUID, C> getChecksMap() {
		return checks;
	}
	
	/**
	 * Gets a collection of all checks associated with this factory
	 * 
	 * @return the collection of this factory's checks
	 */
	protected Collection<C> getChecks() {
		return checks.values();
	}
	
	/*
	 * Framework infrastructure
	 */
	
	private void checkAsyncPeriodic() {
		if (!started()) {
			return;
		}
		checks.values().forEach((check) -> check.checkAsyncPeriodic());
	}
	
	@Override
	C newCheck(NessPlayer nessPlayer) {
		C check = instantiator.newCheck(this, nessPlayer);
		checks.put(nessPlayer.getUniqueId(), check);
		return check;
	}
	
	@Override
	void removeCheck(NessPlayer nessPlayer) {
		checks.remove(nessPlayer.getUniqueId());
	}
	
	/*
	 * API methods
	 */
	
	private C getCheck(AnticheatPlayer player) {
		if (!(player instanceof NessPlayer)) {
			// Foreign implementation
			return null;
		}
		return checks.get(player.getUniqueId());
	}
	
	@Override
	public int getViolationCountFor(AnticheatPlayer player) {
		C check = getCheck(player);
		if (check == null) {
			return -1;
		}
		return check.currentViolationCount();
	}
	
	@Override
	public FlagResult flagHack(AnticheatPlayer player) {
		C check = getCheck(player);
		if (check == null) {
			return FlagResult.notTracking();
		}
		if (!check.callFlagEvent()) {
			return FlagResult.eventCancelled();
		}
		Infraction infraction = check.flag0("");
		return FlagResult.success(infraction);
	}
	
	@Override
	public boolean isTracking(AnticheatPlayer player) {
		return getCheck(player) != null;
	}
	
	/*
	 * Start and stop
	 */
	
	@Override
	protected synchronized void start() {
		if (getCheckInfo().hasAsyncInterval()) {
			Duration asyncInterval = getCheckInfo().getAsyncInterval();
			scheduledFuture = getCheckManager().getNess().getExecutor().scheduleWithFixedDelay(
					this::checkAsyncPeriodic, 0L, asyncInterval.toNanos(), TimeUnit.NANOSECONDS);

		} else {
			scheduledFuture = null;
		}

		super.start();
	}
	
	@Override
	protected synchronized void close() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
		}

		super.close();
	}
	
}
