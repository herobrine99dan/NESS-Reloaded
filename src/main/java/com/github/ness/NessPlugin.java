package com.github.ness;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.ness.blockgetter.MaterialAccess;

import io.github.retrooper.packetevents.PacketEvents;

public class NessPlugin extends JavaPlugin {

    /**
     * This class contains method that load, enable and disable the plugin This
     * class prevents errors caused by /reload command or plugins that do the same
     * thing (PlugMan or others)
     * 
     * @author A248 with some changes from herobrine99dan
     * @since 2.8
     */

    private NessAnticheat ness;
    private Class<?> materialAccessImplClass;

    @Override
    public synchronized void onLoad() {
        if (ness != null) {
            throw new IllegalStateException("Already enabled and running");
        }
        PacketEvents.load();
        try {
            MaterialAccess.class.hashCode(); // The bukkit class loader must load this class to execute the stupid
                                             // method
            NessClassLoader loader = new NessClassLoader(new URL[] { this.getFile().toURI().toURL() },
                    this.getClassLoader());
            materialAccessImplClass = loader.findClass("com.github.ness.blockgetter.MaterialAccessImpl");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public synchronized void onEnable() {
        if (ness != null) {
            throw new IllegalStateException("Already enabled and running");
        }
        NessAnticheat ness = new NessAnticheat(this, getDataFolder().toPath(), materialAccessImplClass);
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
