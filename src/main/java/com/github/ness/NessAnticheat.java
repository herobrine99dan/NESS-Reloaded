package com.github.ness;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import com.github.ness.antibot.AntiBot;
import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.check.CoreListener;
import com.github.ness.listener.BungeeCordListener;
import com.github.ness.packets.NessPacketListener;
import com.github.ness.violation.ViolationHandler;

import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;

public class NessAnticheat {

    private static final Logger logger = NessLogger.getLogger(NessAnticheat.class);

    private final JavaPlugin plugin;
    private static final int minecraftVersion;
    private final ScheduledExecutorService executor;

    private final CheckManager checkManager;
    @Getter
    private final ViolationHandler violationHandler;
    @Getter
    private final AntiBot antiBot;
    @Getter
    private MaterialAccess materialAccess;
    @Getter
    private NessConfig nessConfig;

    static {
        minecraftVersion = getVersion();
    }

    NessAnticheat(JavaPlugin plugin, Path folder, MaterialAccess materialAccess) {
        this.plugin = plugin;
        executor = Executors.newSingleThreadScheduledExecutor();
        this.violationHandler = new ViolationHandler(this);
        checkManager = new CheckManager(this);
        checkManager.initialize();
        antiBot = new AntiBot(this);
        this.materialAccess = materialAccess;
        this.nessConfig = new NessConfig("config.yml", "messages.yml");
    }

    private static int getVersion() {
        String first = Bukkit.getVersion().substring(Bukkit.getVersion().indexOf("(MC: "));
        return Integer.valueOf(first.replace("(MC: ", "").replace(")", "").replace(" ", "").replace(".", ""));
    }

    void start() {
        this.nessConfig.reloadConfiguration(this);
        if (!this.nessConfig.checkConfigVersion())
            logger.warning(
                    "Your config.yml is outdated! Until you regenerate it, NESS will use default values for some checks.");
        if (!this.nessConfig.checkMessagesVersion())
            logger.warning(
                    "Your messages.yml is outdated! Until you regenerate it, NESS will use default values for some messages.");
        Bukkit.getServer().getPluginManager().registerEvents(new CoreListener(this.getCheckManager()), plugin);
        // Start configuration
        logger.fine("Configuration loaded. Initiating checks...");
        this.getPlugin().saveDefaultConfig();
        // Start checks
        logger.fine("Starting CheckManager");
        if (!Bukkit.getName().toLowerCase().contains("glowstone")) {
            NessPacketListener listener = new NessPacketListener(this);
            Bukkit.getPluginManager().registerEvents(listener, this.getPlugin());
            PacketEvents.getAPI().getEventManager().registerListener(listener);
        }
        if (this.getPlugin().getConfig().getBoolean("violation-handling.notify-staff.bungeecord")) {
            Messenger messenger = plugin.getServer().getMessenger();
            messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
            messenger.registerIncomingPluginChannel(plugin, "BungeeCord", new BungeeCordListener());
        }
        if (this.getPlugin().getConfig().getBoolean("antibot.enable")) {
            this.antiBot.initiate();
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
        executor.shutdown();
        try {
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        /*
         * try { executor.shutdown(); executor.awaitTermination(10L, TimeUnit.SECONDS);
         * } catch (InterruptedException ex) { logger.log(Level.WARNING,
         * "Failed to complete thread pool termination", ex); }
         */
    }

}