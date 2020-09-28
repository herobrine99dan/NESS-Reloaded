package com.github.ness;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.antibot.AntiBot;
import com.github.ness.api.NESSApi;
import com.github.ness.check.CheckManager;
import com.github.ness.config.ConfigManager;
import com.github.ness.config.NessConfig;
import com.github.ness.config.NessMessages;
import com.github.ness.listener.BungeeCordListener;
import com.github.ness.packets.PacketListener;
import com.github.ness.violation.ViolationManager;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {

	private static final Logger logger = NessLogger.getLogger(NESSAnticheat.class);
	static NESSAnticheat main;

	@Getter
	private ScheduledExecutorService executor;

	private ConfigManager configManager;
	@Getter
	private CheckManager checkManager;
	@Getter
	private ViolationManager violationManager;
	@Getter
	private int minecraftVersion;

	public static NESSAnticheat getInstance() {
		return NESSAnticheat.main;
	}

	@Override
	public void onEnable() {
		main = this;

		// Detect version
		minecraftVersion = getVersion();
		if (minecraftVersion > 1152 && minecraftVersion < 1162) {
			logger.warning("Please use 1.16.2 Spigot Version since 1.16/1.16.1 has a lot of false flags");
		}

		// Start configuration
		configManager = new ConfigManager(getDataFolder().toPath());
		configManager.reload().join();
		logger.fine("Configuration loaded. Initiating checks...");

		// Start executor service & commands
		executor = Executors.newSingleThreadScheduledExecutor();
		getCommand("ness").setExecutor(new NESSCommands(this));

		// Start checks
		checkManager = new CheckManager(this);
		logger.fine("Starting CheckManager");
		CompletableFuture<?> future = checkManager.start();
		getServer().getScheduler().runTaskLater(this, future::join, 1L);

		// Start violation handling
		violationManager = new ViolationManager(this);
		violationManager.initiate();

		// Register API implementation
		getPlugin().getServer().getServicesManager()
				.register(NESSApi.class, new NESSApiImpl(this), this, ServicePriority.Low);

		// Start AntiBot if enabled
		if (getMainConfig().getAntiBot().enable()) {
			AntiBot antiBot = new AntiBot(this, getMainConfig().getAntiBot());
			antiBot.initiate();
		}

		// Start packet listener except on Glowstone
		if (!Bukkit.getName().toLowerCase().contains("glowstone")) {
			getServer().getPluginManager().registerEvents(new PacketListener(this), this);
		}
		// Start plugin message listener if bungeecord notify-staff hook enabled
		if (getMainConfig().getViolationHandling().notifyStaff().bungeecord()) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordListener());
		}
	}
	
	/**
	 * Gets the {@code JavaPlugin} NESS is using. Although this is currently coupled to this class,
	 * separation may happen in the future.
	 * 
	 * @return the java plugin
	 */
	public JavaPlugin getPlugin() {
		return this;
	}
	
	ConfigManager getConfigManager() {
		return configManager;
	}
	
	public NessConfig getMainConfig() {
		return configManager.getConfig();
	}
	
	public NessMessages getMessagesConfig() {
		return configManager.getMessages();
	}

	private int getVersion() {
		String first = Bukkit.getVersion().substring(Bukkit.getVersion().indexOf("(MC: "));
		return Integer.valueOf(first.replace("(MC: ", "").replace(")", "").replace(" ", "").replace(".", ""));
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
				logger.log(Level.WARNING, "Failed to complete thread pool termination", ex);
			}
		}
	}

}