package com.github.ness;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {

	@Getter
	private ScheduledExecutorService executor;
	public static NESSAnticheat main;
	@Getter
	private NessConfig nessConfig;
	public boolean devMode = true;
	private CheckManager manager;

	@Override
	public void onEnable() {
		main = this;
		String cfg = "config-v2.yml";
		saveResource(cfg, false);
		nessConfig = new NessConfig(YamlConfiguration.loadConfiguration(new File(getDataFolder(), cfg)));
		if (!nessConfig.checkVersion()) {
			getLogger().warning(
					"Your config is outdated! Until you regenerate it, NESS will use default values for some checks.");
		}
		executor = Executors.newSingleThreadScheduledExecutor();
		manager = new CheckManager(this);
		CompletableFuture<?> future = manager.loadAsync();
		getServer().getScheduler().runTaskLater(this, future::join, 1L);
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