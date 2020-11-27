package com.github.ness;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.retrooper.packetevents.PacketEvents;

public class NessPlugin extends JavaPlugin {
    
    /**
     * This class contains method that load, enable and disable the plugin
     * This class prevents errors caused by /reload command or plugins that do the same thing (PlugMan or others)
     * @author A248 with some changes from herobrine99dan
     * @since 2.8
     */

    private NessAnticheat ness;
    
    @Override
    public synchronized void onLoad() {
        if (ness != null) {
            throw new IllegalStateException("Already enabled and running");
        }
        PacketEvents.load();
    }

    @Override
    public synchronized void onEnable() {
        if (ness != null) {
            throw new IllegalStateException("Already enabled and running");
        }
        NessAnticheat ness = new NessAnticheat(this, getDataFolder().toPath());
        ness.start();
        getCommand("ness").setExecutor(new NessCommands(ness));
        this.ness = ness;
        PacketEvents.getSettings().injectAsync(true);
        PacketEvents.getSettings().ejectAsync(true);
        PacketEvents.init(this);
    }

    @Override
    public synchronized void onDisable() {
        if (ness == null) {
            getLogger().warning("No running instance of NESS. Did an error occur at startup?");
            return;
        }
        PacketEvents.stop();
        try {
            ness.close();
        } finally {
            ness = null;
        }
    }

}
