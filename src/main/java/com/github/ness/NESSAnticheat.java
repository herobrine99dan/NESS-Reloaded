package com.github.ness;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
	@Getter
	private Executor syncExecutor;
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

	@Override
	public void onEnable() {
		main = this;
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
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		this.nmsHandler = nmsHandler;

		executor = Executors.newSingleThreadScheduledExecutor();
		syncExecutor = (cmd) -> Bukkit.getScheduler().runTask(this, cmd);
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
		try {
			Class<?> handlerClass = Class.forName("com.github.ness.nms.NMS_" + nmsVersion);
			if (NMSHandler.class.isAssignableFrom(handlerClass)) {
				return (NMSHandler) handlerClass.newInstance();
			}
		} catch (ClassNotFoundException ex) {
			getLogger().warning("Your server's version (" + nmsVersion + ") is not recognised.");
		} catch (InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void onDisable() {
		try {
			checkManager.close();
			executor.shutdown();
			executor.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
	}

}