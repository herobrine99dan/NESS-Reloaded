package com.github.ness;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ness.api.NESSApi;
import com.github.ness.packets.NewPacketListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {
	@Getter
	private ScheduledExecutorService executor;
	static NESSAnticheat main;
	@Getter
	private NessConfig nessConfig;
	@Getter
	private CheckManager checkManager;
	@Getter
	private ViolationManager violationManager;
	@Getter
	private int minecraftVersion;
	
	private static final Logger logger = LogManager.getLogger(NESSAnticheat.class);

	@Override
	public void onEnable() {
		main = this;
		logger.info("NESS uses log4j2 for logging");
		
		nessConfig = new NessConfig("config.yml", "messages.yml");
		nessConfig.reloadConfiguration(this);
		if (!nessConfig.checkConfigVersion()) {
			getLogger().warning(
					"Your config.yml is outdated! Until you regenerate it, NESS will use default values for some checks.");
		}
		if (!nessConfig.checkMessagesVersion()) {
			getLogger().warning(
					"Your messages.yml is outdated! Until you regenerate it, NESS will use default values for some messages.");
		}
		logger.debug("Configuration loaded. Initiating checks...");

		executor = Executors.newSingleThreadScheduledExecutor();
		getCommand("ness").setExecutor(new NessCommands(this));

		checkManager = new CheckManager(this);
		CompletableFuture<?> future = checkManager.loadChecks();
		getServer().getPluginManager().registerEvents(checkManager.coreListener, this);

		violationManager = new ViolationManager(this);
		violationManager.addDefaultActions();
		violationManager.initiatePeriodicTask();
		getServer().getPluginManager().registerEvents(new NewPacketListener(), this);
		Scheduler.startSyncScheduler();
		// new Protocols();

		getServer().getScheduler().runTaskLater(this, future::join, 1L);

		getServer().getServicesManager().register(NESSApi.class, new NESSApiImpl(this), this, ServicePriority.Low);
		minecraftVersion = this.getVersion();
		if (this.getNessConfig().getViolationHandling().getConfigurationSection("notify-staff").getBoolean("bungeecord", false)) {
		    this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordListener());
		}
	}
	
	public int getVersion() {
		String first = Bukkit.getVersion().substring(Bukkit.getVersion().indexOf("(MC: "));
		return Integer.valueOf(first.replace("(MC: ", "").replace(")", "").replace(" ", "").replace(".", ""));
	}
	
	public static NESSAnticheat getInstance() {
		return NESSAnticheat.main;
	}

	@Override
	public void onDisable() {
		if (checkManager != null) {
			checkManager.close();
		}
		if (executor != null) {
			try {
				executor.shutdown();
				executor.awaitTermination(10L, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				logger.warn("Failed to complete thread pool termination", ex);
			}
		}
	}

}