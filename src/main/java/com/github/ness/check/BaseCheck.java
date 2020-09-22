package com.github.ness.check;

import java.time.Duration;

import com.github.ness.NESSAnticheat;

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
	
	protected NESSAnticheat ness() {
		return manager().getNess();
	}
	
	/**
	 * Runs a delayed task using the bukkit scheduler
	 * 
	 * @param command the runnable to run later
	 * @param duration the delay
	 */
	protected void runTaskLater(Runnable command, Duration duration) {
		manager().getNess().getServer().getScheduler().runTaskLater(manager().getNess(), command, duration.toMillis() / 50L);
	}
	
	protected Duration durationOfTicks(int ticks) {
		return Duration.ofMillis(ticks * 50L);
	}
	
}
