package com.github.ness;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.nms.NMSHandler;
import com.github.ness.protocol.TinyProtocol;
import com.github.ness.protocol.TinyProtocolListeners;

import lombok.Getter;

public class NESSAnticheat extends JavaPlugin {
    @Getter
	public TinyProtocol protocol;

	@Getter
	private ScheduledExecutorService executor;
	public static NESSAnticheat main;
	@Getter
	private NessConfig nessConfig;
	public boolean devMode = true;
	@Getter
	private CheckManager checkManager;
	@Getter
	private NMSHandler nmsHandler;

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
		NMSHandler nmsHandler = findNMSHandler();
		if (nmsHandler == null) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		this.nmsHandler = nmsHandler;

		executor = Executors.newSingleThreadScheduledExecutor();
		getServer().getPluginCommand("ness").setExecutor(new NessCommands(this));
		checkManager = new CheckManager(this);

		this.protocol = (TinyProtocol) new TinyProtocolListeners((Plugin) this);
		new Scheduler().start();
		//new Protocols();

		CompletableFuture<?> future = checkManager.loadAsync();
		getServer().getScheduler().runTaskLater(this, future::join, 1L);
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
		checkManager.close();
		executor.shutdown();
		try {
			executor.awaitTermination(10L, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	
}