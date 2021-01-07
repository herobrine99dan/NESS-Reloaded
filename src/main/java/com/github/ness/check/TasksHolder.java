package com.github.ness.check;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class TasksHolder implements AutoCloseable {

	private BukkitTask syncTask;
	private ScheduledFuture<?> scheduledFuture;

	void startSync(PeriodicTaskInfo info, CheckManager checkManager, Runnable toCallSyncPeriodic) {
		Duration syncInterval = info.syncInterval();
		if (syncInterval != null) {
			JavaPlugin plugin = checkManager.getNess().getPlugin();
			syncTask = plugin.getServer().getScheduler().runTaskTimer(
					plugin, toCallSyncPeriodic, 0L, syncInterval.toMillis() / 50L);
		}

	}

	void startAsync(PeriodicTaskInfo info, CheckManager checkManager, Runnable toCallAsyncPeriodic) {
		Duration asyncInterval = info.asyncInterval();
		if (asyncInterval != null) {
			scheduledFuture = checkManager.getNess().getExecutor().scheduleWithFixedDelay(
					toCallAsyncPeriodic, 0L, asyncInterval.toNanos(), TimeUnit.NANOSECONDS);
		}
	}

	@Override
	public void close() {
		if (syncTask != null) {
			syncTask.cancel();
		}
		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
		}
	}
}
