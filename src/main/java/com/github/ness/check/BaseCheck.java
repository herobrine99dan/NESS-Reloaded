package com.github.ness.check;

import java.time.Duration;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.NessAnticheat;
import com.github.ness.blockgetter.MaterialAccess;

public class BaseCheck {

	private final BaseCheckFactory<?> factory;
	
	protected BaseCheck(BaseCheckFactory<?> factory) {
		this.factory = factory;
	}
	
	protected BaseCheckFactory<?> getFactory() {
		return factory;
	}
	
	/*
	 * Helper methods
	 */
	
	protected CheckManager manager() {
		return getFactory().getCheckManager();
	}
	
	protected MaterialAccess getMaterialAccess() {
		return this.manager().getNess().getMaterialAccess();
	}
	
	protected NessAnticheat ness() {
		return manager().getNess();
	}
	
	protected MaterialAccess materialAccess() {
		return ness().getMaterialAccess();
	}
	
	/**
	 * Runs a delayed task using the bukkit scheduler
	 * 
	 * @param command the runnable to run later
	 * @param duration the delay
	 */
	protected void runTaskLater(Runnable command, Duration duration) {
		JavaPlugin plugin = manager().getNess().getPlugin();
		plugin.getServer().getScheduler().runTaskLater(plugin, command, duration.toMillis() / 50L);
	}
	
	protected Duration durationOfTicks(int ticks) {
		return Duration.ofMillis(ticks * 50L);
	}
	
}
