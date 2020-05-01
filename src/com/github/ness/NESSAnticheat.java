package com.github.ness;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {

	@Getter
	private ScheduledExecutorService executor;
	
	// TODO add config (this is null for now)
	@Getter
	private NessConfig nessConfig;
	
	private CheckManager manager;
	
	@Override
	public void onEnable() {
		executor = Executors.newSingleThreadScheduledExecutor();
		manager = new CheckManager(this);
		manager.registerListener();
		manager.addAllChecks();
	}
	
	@Override
	public void onDisable() {
		manager.close();
		executor.shutdown();
		try {
			executor.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	
}
