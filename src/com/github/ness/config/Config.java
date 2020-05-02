package com.github.ness.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;

public class Config {

	private final FileConfiguration config;
	private final File file;
	private final ConfigManager manager;
	private final Plugin plugin;
	private int comments;
	private int lines;

	Config(final InputStream input, final File file, final int comments, final Plugin plugin) {
		this.plugin = plugin;
		this.comments = comments;
		this.manager = new ConfigManager(plugin);
		this.file = file;
		this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
		this.lines = this.config.toString().split("\n").length;
	}

	public Object get(final String path) {
		return this.config.get(path);
	}

	public void overwrite(final String path, final Config config, final Object defaultvalue, final String... comments) {
		if (config.get(path) == null) {
			this.set(path, defaultvalue, comments);
		} else {
			this.set(path, config.get(path), comments);
		}
	}

	public void saveConfig() {
		this.manager.saveConfig(this.config.saveToString(), this.file);
	}

	public void set(final String path, final Object value, final String... comments) {
		this.config.set(path, value);
		this.lines = this.config.saveToString().split("\n").length - this.comments;
		int spaceAmount;
		if (path.contains(".")) {
			spaceAmount = (path.split("\\.").length - 1) * 2;
		} else {
			spaceAmount = 0;
		}
		for (final String comm : comments) {
			this.lines = this.config.saveToString().split("\n").length - this.comments;
			this.config.set(this.plugin.getDescription().getPrefix() + "_COMMENT_" + (this.lines) + "_" + spaceAmount, comm);
			this.comments++;
		}
	}
}
