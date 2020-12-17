package com.github.ness.check;

import com.github.ness.NessPlayer;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.FlagResult;
import com.github.ness.api.Infraction;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Check factory for all per player checks
 * 
 * @author A248
 *
 * @param <C> the type of the check
 */
public class CheckFactory<C extends Check> extends BaseCheckFactory<C> {

	private final CheckInstantiator<C> instantiator;

	private final TasksHolder tasksHolder = new TasksHolder();
	private final ConcurrentMap<UUID, C> checks = new ConcurrentHashMap<>();
	
	protected CheckFactory(CheckInstantiator<C> instantiator, String checkName,
						   CheckManager manager, CheckInfo checkInfo) {
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

	private void checkSyncPeriodic() {
		checks.values().forEach(Check::checkSyncPeriodicUnlessInvalid);
	}

	private void checkAsyncPeriodic() {
		if (!started()) {
			return;
		}
		checks.values().forEach(Check::checkAsyncPeriodicUnlessInvalid);
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
		PeriodicTaskInfo taskInfo = getCheckInfo().taskInfo();
		tasksHolder.startSync(taskInfo, getCheckManager(), this::checkSyncPeriodic);
		tasksHolder.startAsync(taskInfo, getCheckManager(), this::checkAsyncPeriodic);

		super.start();
	}
	
	@Override
	protected synchronized void close() {
		tasksHolder.close();
		super.close();
	}
	
}
