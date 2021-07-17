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
import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.check.CheckManager;
import com.github.ness.config.ConfigManager;
import com.github.ness.config.NessConfig;
import com.github.ness.config.NessMessages;
import com.github.ness.data.MovementValues;
import com.github.ness.listener.BungeeCordListener;
import com.github.ness.packets.NetworkReflectionCreation;
import com.github.ness.packets.Networker;
import com.github.ness.packets.PacketActorInterceptor;
import com.github.ness.packets.PacketListener;
import com.github.ness.packets.wrapper.PacketTypeRegistry;
import com.github.ness.packets.wrapper.SimplePacketTypeRegistry;
import com.github.ness.reflect.CoreReflection;
import com.github.ness.reflect.InvokerCachingReflection;
import com.github.ness.reflect.MethodHandleReflection;
import com.github.ness.reflect.ReflectHelper;
import com.github.ness.reflect.Reflection;
import com.github.ness.reflect.SimpleReflectHelper;
import com.github.ness.reflect.locator.ClassLocator;
import com.github.ness.reflect.locator.VersionDetermination;
import com.github.ness.violation.ViolationManager;

public class NessAnticheat {

	private static final Logger logger = NessLogger.getLogger(NessAnticheat.class);

	private final JavaPlugin plugin;
	private final ScheduledExecutorService executor;
	private final VersionDetermination versionDetermination;
	private final ConfigManager configManager;
	private final CheckManager checkManager;
	private final Networker networker;
	private final ViolationManager violationManager;
	private final MaterialAccess materialAccess;
	private final ReflectHelper reflectHelper;
	private final PacketTypeRegistry packetTypeRegistry;
	private final boolean useFloodGate;

	NessAnticheat(JavaPlugin plugin, Path folder, MaterialAccess materialAccess) {
		this.plugin = plugin;
		// We setup the MovementValuesHelper creating an empty MovementValues object
		MovementValues emptyObject = new MovementValues(materialAccess);
		emptyObject.getHelper().calcDamageFall(5); // Executing a method to force Java to load completely the
													// MovementValuesHelper
		executor = Executors.newSingleThreadScheduledExecutor();
		configManager = new ConfigManager(folder);
		checkManager = new CheckManager(this);
		violationManager = new ViolationManager(this);
		this.materialAccess = materialAccess;
		if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
			logger.log(Level.INFO, "Floodgate was detected, NESS Reloaded will fix checks for Geyser's users!");
			useFloodGate = true;
		} else {
			useFloodGate = false;
		}

		ClassLocator locator = ClassLocator.create(plugin.getServer());
		Reflection reflection = new InvokerCachingReflection(new MethodHandleReflection(new CoreReflection()));
		networker = new Networker(plugin,
				new PacketListener(new NetworkReflectionCreation(reflection, locator).create(),
						new PacketActorInterceptor(checkManager::receivePacket, reflection)));
		reflectHelper = new SimpleReflectHelper(locator, reflection);
		String craftbukkitPackage = Bukkit.getServer().getClass().getPackage().getName();
		String nmsVersion = VersionDetermination.getNmsVersion(craftbukkitPackage);
		versionDetermination = new VersionDetermination(nmsVersion);
		packetTypeRegistry = new SimplePacketTypeRegistry(this.versionDetermination, reflectHelper);
	}

	void start() {

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
		getPlugin().getServer().getServicesManager().register(NESSApi.class, new NessApiImpl(this), plugin,
				ServicePriority.Low);

		// Start AntiBot if enabled
		if (getMainConfig().getAntiBot().enable()) {
			AntiBot antiBot = new AntiBot(plugin, getMainConfig().getAntiBot());
			antiBot.initiate();
		}

		// Start packet listener except on Glowstone
		if (!Bukkit.getName().toLowerCase().contains("glowstone")) {
			networker.start();
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

	public MaterialAccess getMaterialAccess() {
		return materialAccess;
	}

	public ReflectHelper getReflectHelper() {
		return reflectHelper;
	}

	public PacketTypeRegistry getPacketTypeRegistry() {
		return packetTypeRegistry;
	}

	public NessConfig getMainConfig() {
		return configManager.getConfig();
	}

	public NessMessages getMessagesConfig() {
		return configManager.getMessages();
	}

	void close() {
		networker.close();
		checkManager.close();
		try {
			executor.shutdown();
			executor.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			logger.log(Level.WARNING, "Failed to complete thread pool termination", ex);
		}
	}

	public boolean isUseFloodGate() {
		return useFloodGate;
	}

	public VersionDetermination getVersionDetermination() {
		return versionDetermination;
	}

}