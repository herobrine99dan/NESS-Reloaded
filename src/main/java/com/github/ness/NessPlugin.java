package com.github.ness;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import com.github.ness.blockgetter.MaterialAccess;

public class NessPlugin extends JavaPlugin {

	private NessAnticheat ness;
	
	@Override
	public synchronized void onEnable() {
		if (ness != null) {
			throw new IllegalStateException("Already enabled and running");
		}
        MaterialAccess materialAccess = null;
        try {
            MaterialAccess.class.hashCode(); // The bukkit class loader must load this class to execute the stupid
                                             // method
            NessClassLoader loader = new NessClassLoader(new URL[] { this.getFile().toURI().toURL() },
                    this.getClassLoader());
            Class<?> materialAccessImplClass = loader.findClass("com.github.ness.blockgetter.MaterialAccessImpl");
            materialAccess = materialAccessImplClass.asSubclass(MaterialAccess.class).getDeclaredConstructor()
                    .newInstance();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("NESS Reloaded can't be started due to an exception!", e);
        }
		NessAnticheat ness = new NessAnticheat(this, getDataFolder().toPath(), materialAccess);
		ness.start();
		getCommand("ness").setExecutor(new NessCommands(ness));
		this.ness = ness;
	}
	
	@Override
	public synchronized void onDisable() {
		if (ness == null) {
			getLogger().warning("No running instance of NESS. Did an error occur at startup?");
			return;
		}
		try {
			ness.close();
		} finally {
			ness = null;
		}
	}
	
}
