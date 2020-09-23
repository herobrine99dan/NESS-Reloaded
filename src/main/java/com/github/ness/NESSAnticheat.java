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
import com.github.ness.api.impl.NESSApiImpl;
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

		configManager = new ConfigManager(getDataFolder().toPath());
		configManager.reload().join();

		logger.fine("Configuration loaded. Initiating checks...");
		if (this.getVersion() > 1152 && this.getVersion() < 1162) {
			getLogger().warning("Please use 1.16.2 Spigot Version since 1.16/1.16.1 has a lot of false flags");
		}
		executor = Executors.newSingleThreadScheduledExecutor();
		getCommand("ness").setExecutor(new NESSCommands(this));

		checkManager = new CheckManager(this);
		logger.log(Level.FINE, "Starting CheckManager");
		CompletableFuture<?> future = checkManager.start();

		violationManager = new ViolationManager(this);
		violationManager.initiate();
		getServer().getScheduler().runTaskLater(this, future::join, 1L);
		if (this.getNessConfig().getConfig().getConfigurationSection("antibot").getBoolean("enable")) {
			AntiBot antiBot = new AntiBot(this);
			getServer().getPluginManager().registerEvents(antiBot, this);
			getServer().getScheduler().runTaskTimer(this, antiBot, 0L, 20L);
		}
		getServer().getServicesManager().register(NESSApi.class, new NESSApiImpl(this), this, ServicePriority.Low);
		minecraftVersion = this.getVersion();
		if (!Bukkit.getName().toLowerCase().contains("glowstone")) {
			getServer().getPluginManager().registerEvents(new PacketListener(), this);
		}
		if (this.getNessConfig().getViolationHandling().getConfigurationSection("notify-staff").getBoolean("bungeecord",
				false)) {
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordListener());
		}
	}
	
	/**
	 * Previous getter for configuration
	 * 
	 * @return the old ness config
	 * @deprecated No longer used, use {@link #getMainConfig()} and {@link #getMessagesConfig()}
	 */
	@Deprecated
	public NESSConfig getNessConfig() {
		throw new UnsupportedOperationException();
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