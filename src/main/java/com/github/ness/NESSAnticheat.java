package com.github.ness;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.api.NESSApi;
import com.github.ness.nms.NMSHandler;
import com.github.ness.protocol.NewPacketListener;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {

	public static boolean use1_15Helper = false;

	@Getter
	private ScheduledExecutorService executor;
	public static NESSAnticheat main;
	@Getter
	private NessConfig nessConfig;
	public boolean devMode = true;
	@Getter
	private CheckManager checkManager;
	@Getter
	private ViolationManager violationManager;
	@Getter
	private NMSHandler nmsHandler;
	
	private static final Logger logger = LoggerFactory.getLogger(NESSAnticheat.class);

	@Override
	public void onEnable() {
		main = this;
		logger.info("NESS uses slf4j for logging");
		logger.warn("NESS warn messages look like this");
		logger.debug("NESS debug messages look like this");
		
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
		NMSHandler nmsHandler = findNMSHandler();
		if (nmsHandler == null) {
			setEnabled(false);
			return;
		}
		this.nmsHandler = nmsHandler;
		logger.debug("Configuration loaded and NMS version detected. Initiating checks...");

		executor = Executors.newSingleThreadScheduledExecutor();
		getServer().getPluginCommand("ness").setExecutor(new NessCommands(this));

		checkManager = new CheckManager(this);
		CompletableFuture<?> future = checkManager.loadAsync();

		violationManager = new ViolationManager(this);
		violationManager.addDefaultActions();
		violationManager.initiatePeriodicTask();
		getServer().getPluginManager().registerEvents(new NewPacketListener(), this);
		Scheduler.startSyncScheduler();
		Scheduler.startAsyncScheduler();
		// new Protocols();

		getServer().getScheduler().runTaskLater(this, future::join, 1L);

		getServer().getServicesManager().register(NESSApi.class, new NESSApiImpl(this), this, ServicePriority.Low);
	}

	private NMSHandler findNMSHandler() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName(); // org.bukkit.craftbukkit.v1_8_R3
		String nmsVersion = packageName.substring("org.bukkit.craftbukkit.v".length()); // 1_8_R3
		logger.debug("NMS version {}", nmsVersion);
		try {
			Class<?> handlerClass = Class.forName("com.github.ness.nms.NMS_" + nmsVersion);
			if (NMSHandler.class.isAssignableFrom(handlerClass)) {
				return (NMSHandler) handlerClass.newInstance();
			}
		} catch (ClassNotFoundException ex) {
			logger.warn("Your server's version ({}) is not recognised.", nmsVersion);
		} catch (InstantiationException | IllegalAccessException ex) {
			logger.warn("Could not determine server version / NMS handler", ex);
		}
		return null;
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
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}
	}

}