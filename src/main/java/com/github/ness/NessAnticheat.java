package com.github.ness;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.check.CoreListener;
import com.github.ness.packets.PacketListener;

public class NessAnticheat {

    private static final Logger logger = NessLogger.getLogger(NessAnticheat.class);

    private final JavaPlugin plugin;
    private final static int minecraftVersion;
    private final ScheduledExecutorService executor;

    private final CheckManager checkManager;

    static {
        minecraftVersion = getVersion();
    }

    // This Line is so useful!
    NessAnticheat(JavaPlugin plugin, Path folder) {
        this.plugin = plugin;
        executor = Executors.newSingleThreadScheduledExecutor();

        checkManager = new CheckManager(this);
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
        Check check = new TestCheck(null, this.getCheckManager());
        Bukkit.getServer().getPluginManager().registerEvents(new CoreListener(this.getCheckManager()), plugin);
        // Start configuration
        logger.fine("Configuration loaded. Initiating checks...");

        // Start checks
        logger.fine("Starting CheckManager");
        // CompletableFuture<?> future = checkManager.start();
        // plugin.getServer().getScheduler().runTaskLater(plugin, future::join, 1L);

        // Start violation handling

        // Register API implementation
        // getPlugin().getServer().getServicesManager().register(NESSApi.class, new
        // NessApiImpl(this), plugin,
        // ServicePriority.Low);

        // Start AntiBot if enabled

        // Start packet listener except on Glowstone
        if (!Bukkit.getName().toLowerCase().contains("glowstone")) {
            plugin.getServer().getPluginManager().registerEvents(new PacketListener(this), plugin);
        }
        // Start plugin message listener if bungeecord notify-staff hook enabled
        /*
         * if (getMainConfig().getViolationHandling().notifyStaff().bungeecord()) {
         * Messenger messenger = plugin.getServer().getMessenger();
         * messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
         * messenger.registerIncomingPluginChannel(plugin, "BungeeCord", new
         * BungeeCordListener()); }
         */
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
    public static int getMinecraftVersion() {
        return minecraftVersion;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public CheckManager getCheckManager() {
        return checkManager;
    }

    // public ViolationManager getViolationManager() {
    // return violationManager;
    // }

    void close() {
        // checkManager.close();
        try {
            executor.shutdown();
            executor.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.log(Level.WARNING, "Failed to complete thread pool termination", ex);
        }
    }

}