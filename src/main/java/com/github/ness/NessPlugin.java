package com.github.ness;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import com.github.ness.blockgetter.MaterialAccess;

import com.github.ness.packets.NetworkReflectionCreation;
import com.github.ness.reflect.CoreReflection;
import com.github.ness.reflect.SimpleClassLocator;
import org.bukkit.plugin.java.JavaPlugin;

public class NessPlugin extends JavaPlugin {

	private NessAnticheat ness;
	private NessClassLoader materialAccessClassLoader;
	
	@Override
	public synchronized void onEnable() {
		debugReflection();

		if (ness != null) {
			throw new IllegalStateException("Already enabled and running");
		}
		materialAccessClassLoader = createMaterialAccessClassLoader();
		MaterialAccess materialAccess;
		try {
			Class<?> implClass = materialAccessClassLoader.findClass("com.github.ness.blockgetter.MaterialAccessImpl");
			materialAccess = implClass.asSubclass(MaterialAccess.class).getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException ex) {
			throw new IllegalStateException(ex);
		}
		NessAnticheat ness = new NessAnticheat(this, getDataFolder().toPath(), materialAccess);
		ness.start();
		getCommand("ness").setExecutor(new NessCommands(ness));
		this.ness = ness;
	}

	private void debugReflection() {
		new NetworkReflectionCreation(new CoreReflection(), SimpleClassLocator.create()).create();
	}

	private NessClassLoader createMaterialAccessClassLoader() {
		URL jarFile;
		try {
			jarFile = getFile().toURI().toURL();
		} catch (MalformedURLException ex) {
			throw new UncheckedIOException("Unable to represent plugin jar as URL", ex);
		}
		return new NessClassLoader(new URL[] {jarFile},
					getClass().getClassLoader());
	}

	@Override
	public synchronized void onDisable() {
		if (ness == null) {
			getLogger().warning("No running instance of NESS. Did an error occur at startup?");
			return;
		}
		try {
			materialAccessClassLoader.close();
			ness.close();
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		} finally {
			ness = null;
		}
	}
	
}
