package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;

import com.github.ness.utility.HandlerListUtils;

import org.bukkit.event.Event;

class BaseCheckFactory<C extends AbstractCheck<?>> {

	private final Constructor<C> constructor;
	private final CheckManager manager;
	private final ScheduledFuture<?> scheduledFuture;
	
	private final Set<C> checks = new CopyOnWriteArraySet<>();
	
	BaseCheckFactory(Constructor<C> constructor, CheckManager manager, CheckInfo<?> checkInfo) {
		this.constructor = constructor;
		this.manager = manager;
		if (checkInfo.asyncInterval == -1) {
			scheduledFuture = null;
		} else {
			scheduledFuture = manager.getNess().getExecutor().scheduleWithFixedDelay(
					this::checkAsyncPeriodic, 0L, checkInfo.asyncInterval, checkInfo.units);
		}
	}
	
	Set<C> getChecks() {
		return checks;
	}
	
	private void checkAsyncPeriodic() {
		manager.forEachPlayer((nessPlayer) -> {
			for (C check : checks) {
				check.checkAsyncPeriodic(nessPlayer);
			}
		});
	}
	
	static <E extends Event, C extends AbstractCheck<E>> BaseCheckFactory<C> create(Constructor<C> constructor,
			CheckManager manager, CheckInfo<E> info) {
		if (info.event != null) {
			ListeningCheckFactory<E, C> lcf = new ListeningCheckFactory<>(constructor, manager, info);
			HandlerListUtils.getEventListeners(info.event).register(lcf.getScalableListener());
			return lcf;
		}
		return new BaseCheckFactory<>(constructor, manager, info);
	}
	
	C newCheck() {
		C check;
		try {
			check = constructor.newInstance(manager);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException ex) {
			throw new IllegalStateException(
					"Unable to instantiate check " + constructor.getDeclaringClass().getName(), ex);
		}
		checks.add(check);
		return check;
	}
	
	void close() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
		}
	}
	
}
