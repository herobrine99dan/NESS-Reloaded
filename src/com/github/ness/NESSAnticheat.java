package com.github.ness;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {

	@Getter
	private Executor executor;
	
	// TODO add config (this is null for now)
	@Getter
	private NessConfig nessConfig;
	
	private CheckManager manager;
	
	@Override
	public void onEnable() {
		executor = Executors.newSingleThreadExecutor();
		manager = new CheckManager(this);
		manager.registerListener();
		manager.startAsyncTimer();
	}
	
	@Override
	public void onDisable() {
		manager.unregisterListeners();
		ExecutorService service = ((ExecutorService) executor);
		service.shutdown();
		try {
			service.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	
}
