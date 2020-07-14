package com.github.ness;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.ness.check.AbstractCheck;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NessConfig {

	private static final int CONFIG_VERSION = 2;
	private static final int MESSAGES_VERSION = 1;

	private final String cfgFileName;
	private final String msgsFileName;
	private YamlConfiguration config;
	@Getter(AccessLevel.PACKAGE)
	private YamlConfiguration messages;

	void reloadConfiguration(NESSAnticheat ness) {
		File dataFolder = ness.getDataFolder();
		File cfgFile = new File(dataFolder, cfgFileName);
		File msgsFile = new File(dataFolder, msgsFileName);
		if (!cfgFile.exists()) {
			ness.saveResource(cfgFileName, false);
		}
		if (!msgsFile.exists()) {
			ness.saveResource(msgsFileName, false);
		}
		config = YamlConfiguration.loadConfiguration(cfgFile);
		messages = YamlConfiguration.loadConfiguration(msgsFile);
	}

	boolean checkConfigVersion() {
		return config.getInt("config-version", -1) == CONFIG_VERSION;
	}

	boolean checkMessagesVersion() {
		return messages.getInt("messages-version", -1) == MESSAGES_VERSION;
	}

	boolean isDevMode() {
		return config.getBoolean("dev-mode", false);
	}

	List<String> getEnabledChecks() {
		return config.getStringList("enabled-checks");
	}

	public ConfigurationSection getViolationHandling() {
		return config.getConfigurationSection("violation-handling");
	}
	
	public ConfigurationSection getCheck(Class<? extends AbstractCheck<?>> check) {
		return config.getConfigurationSection("checks." + check.getSimpleName().toLowerCase());
	}

}
