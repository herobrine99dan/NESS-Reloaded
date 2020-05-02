package com.github.ness;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class NessConfig {

	private final YamlConfiguration config;
	
	NessConfig(YamlConfiguration config) {
		this.config = config;
	}
	
	public List<String> getEnabledChecks() {
		return config.getStringList("enabled-checks");
	}
	
	public ConfigurationSection getCheck(Class<?> check) {
		return config.getConfigurationSection("checks." + check.getSimpleName());
	}
	
}
