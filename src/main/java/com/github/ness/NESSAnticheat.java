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
	@Getter
	private CheckManager checkManager;

	@Override
	public void onEnable() {
		main = this;
		String cfgYml = "config.yml";
		String msgsYml = "messages.yml";
		saveResource(cfgYml, false);
		saveResource(msgsYml, false);
		nessConfig = new NessConfig(
				YamlConfiguration.loadConfiguration(new File(getDataFolder(), cfgYml)),
				YamlConfiguration.loadConfiguration(new File(getDataFolder(), msgsYml)));
		if (!nessConfig.checkConfigVersion()) {
			getLogger().warning(
					"Your config.yml is outdated! Until you regenerate it, NESS will use default values for some checks.");
		}
		if (!nessConfig.checkMessagesVersion()) {
			getLogger().warning(
					"Your messages.yml is outdated! Until you regenerate it, NESS will use default values for some messages.");
		}
		executor = Executors.newSingleThreadScheduledExecutor();
		getServer().getPluginCommand("ness").setExecutor(new NessCommands(this));
		checkManager = new CheckManager(this);
		new Scheduler().start();
		new Protocols();
		CompletableFuture<?> future = checkManager.loadAsync();
		getServer().getScheduler().runTaskLater(this, future::join, 1L);
	}
	
	@Override
	public void onDisable() {
		checkManager.close();
		executor.shutdown();
		try {
			executor.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	
}