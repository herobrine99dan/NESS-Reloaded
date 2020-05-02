package com.github.ness;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.ness.check.AbstractCheck;

public class NessConfig {

	private final YamlConfiguration config;
	
	NessConfig(YamlConfiguration config) {
		this.config = config;
	}
	
	List<String> getEnabledChecks() {
		return config.getStringList("enabled-checks");
	}
	
	public ConfigurationSection getCheck(Class<? extends AbstractCheck<?>> check) {
		return config.getConfigurationSection("checks." + check.getSimpleName().toLowerCase());
	}
	
}
