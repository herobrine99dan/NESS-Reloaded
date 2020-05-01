package com.github.ness;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class NessConfig {

	private final YamlConfiguration config;
	
	NessConfig(YamlConfiguration config) {
		this.config = config;
	}
	
	public ConfigurationSection getCheck(String check) {
		return config.getConfigurationSection("checks." + check);
	}
	
}
