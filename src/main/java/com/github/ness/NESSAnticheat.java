package com.github.ness;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import com.github.ness.antibot.AntiBot;
import com.github.ness.api.NESSApi;
import com.github.ness.check.CheckManager;
import com.github.ness.config.ConfigManager;
import com.github.ness.config.NessConfig;
import com.github.ness.config.NessMessages;
import com.github.ness.listener.BungeeCordListener;
import com.github.ness.packets.PacketListener;
import com.github.ness.violation.ViolationManager;

public class NESSAnticheat {

	private static final Logger logger = NessLogger.getLogger(NESSAnticheat.class);
	private static NESSAnticheat main;

	private final JavaPlugin plugin;
	private final int minecraftVersion;
	private final ScheduledExecutorService executor;

	private final ConfigManager configManager;
	private final CheckManager checkManager;
	private final ViolationManager violationManager;

	public static NESSAnticheat getInstance() {
		return NESSAnticheat.main;
	}

	NESSAnticheat(JavaPlugin plugin, Path folder) {
		this.plugin = plugin;
		minecraftVersion = getVersion();
		executor = Executors.newSingleThreadScheduledExecutor();

		configManager = new ConfigManager(folder);
		checkManager = new CheckManager(this);
		violationManager = new ViolationManager(this);

		main = this;
	}

	private static int getVersion() {
		String first = Bukkit.getVersion().substring(Bukkit.getVersion().indexOf("(MC: "));
		return Integer.valueOf(first.replace("(MC: ", "").replace(")", "").replace(" ", "").replace(".", ""));
	}
	
	void start() {
		// Detect version
		if (minecraftVersion > 1152 && minecraftVersion < 1162) {
			logger.warning("Please use 1.16.2 Spigot Version since 1.16/1.16.1 has a lot of false flags");
		}

		// Start configuration
		configManager.reload().join();
		logger.fine("Configuration loaded. Initiating checks...");

		// Start checks
		logger.fine("Starting CheckManager");
		CompletableFuture<?> future = checkManager.start();
		plugin.getServer().getScheduler().runTaskLater(plugin, future::join, 1L);

		// Start violation handling
		violationManager.initiate();

		// Register API implementation
		getPlugin().getServer().getServicesManager()
				.register(NESSApi.class, new NESSApiImpl(this), plugin, ServicePriority.Low);

		// Start AntiBot if enabled
		if (getMainConfig().getAntiBot().enable()) {
			AntiBot antiBot = new AntiBot(plugin, getMainConfig().getAntiBot());
			antiBot.initiate();
		}

		// Start packet listener except on Glowstone
		if (!Bukkit.getName().toLowerCase().contains("glowstone")) {
			plugin.getServer().getPluginManager().registerEvents(new PacketListener(this), plugin);
		}
		// Start plugin message listener if bungeecord notify-staff hook enabled
		if (getMainConfig().getViolationHandling().notifyStaff().bungeecord()) {
			Messenger messenger = plugin.getServer().getMessenger();
			messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
			messenger.registerIncomingPluginChannel(plugin, "BungeeCord", new BungeeCordListener());
		}
	}
	
	/**
	 * Gets the {@code JavaPlugin} NESS is using
	 * 
	 * @return the java plugin
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets the detected minecraft version number, e.g. '1162'
	 * 
	 * @return the version number
	 */
	public int getMinecraftVersion() {
		return minecraftVersion;
	}
	
	public ScheduledExecutorService getExecutor() {
		return executor;
	}
	
	ConfigManager getConfigManager() {
		return configManager;
	}
	
	public CheckManager getCheckManager() {
		return checkManager;
	}
	
	public ViolationManager getViolationManager() {
		return violationManager;
	}
	
	public NessConfig getMainConfig() {
		return configManager.getConfig();
	}
	
	public NessMessages getMessagesConfig() {
		return configManager.getMessages();
	}

	void close() {
		checkManager.close();
		try {
			executor.shutdown();
			executor.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			logger.log(Level.WARNING, "Failed to complete thread pool termination", ex);
		}
	}

}