package com.github.ness;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {

	@Getter
	private ScheduledExecutorService executor;
	
	@Getter
	private NessConfig nessConfig;
	
	private CheckManager manager;
	
	@Override
	public void onEnable() {
		nessConfig = new NessConfig(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config-v2.yml")));
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
