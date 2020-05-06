package com.github.ness;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.ness.check.AbstractCheck;

import lombok.AccessLevel;
import lombok.Getter;

public class NessConfig {

	private static final int CONFIG_VERSION = 1;
	private static final int MESSAGES_VERSION = 1;
	
	private final YamlConfiguration config;
	@Getter(AccessLevel.PACKAGE)
	private final YamlConfiguration messages;
	
	NessConfig(YamlConfiguration config, YamlConfiguration messages) {
		this.config = config;
		this.messages = messages;
	}
	
	boolean checkConfigVersion() {
		return config.getInt("config-version", -1) == CONFIG_VERSION;
	}
	
	boolean checkMessagesVersion() {
		return messages.getInt("messages-version", -1) == MESSAGES_VERSION;
	}
	
	List<String> getEnabledChecks() {
		return config.getStringList("enabled-checks");
	}
	
	public ConfigurationSection getCheck(Class<? extends AbstractCheck<?>> check) {
		return config.getConfigurationSection("checks." + check.getSimpleName().toLowerCase());
	}
	
}
