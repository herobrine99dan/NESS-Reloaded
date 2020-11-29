package com.github.ness;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.ness.check.Check;

import lombok.Getter;

public class NessConfig {
    private static final int CONFIG_VERSION = 1;

    private static final int MESSAGES_VERSION = 1;

    private final String cfgFileName;

    private final String msgsFileName;

    @Getter
    private YamlConfiguration config;

    private YamlConfiguration messages;

    public NessConfig(String cfgFileName, String msgsFileName) {
        this.cfgFileName = cfgFileName;
        this.msgsFileName = msgsFileName;
    }

    YamlConfiguration getMessages() {
        return this.messages;
    }

    public void reloadConfiguration(NessAnticheat ness) {
        File dataFolder = ness.getPlugin().getDataFolder();
        File cfgFile = new File(dataFolder, this.cfgFileName);
        File msgsFile = new File(dataFolder, this.msgsFileName);
        if (!cfgFile.exists())
            ness.getPlugin().saveResource(this.cfgFileName, false);
        if (!msgsFile.exists())
            ness.getPlugin().saveResource(this.msgsFileName, false);
        this.config = YamlConfiguration.loadConfiguration(cfgFile);
        this.messages = YamlConfiguration.loadConfiguration(msgsFile);
    }

    public boolean checkConfigVersion() {
        return (this.config.getInt("config-version", -1) == CONFIG_VERSION);
    }

    public boolean checkMessagesVersion() {
        return (this.messages.getInt("messages-version", -1) == MESSAGES_VERSION);
    }

    public boolean isDevMode() {
        return this.config.getBoolean("dev-mode", false);
    }

    public List<String> getEnabledChecks() {
        return this.config.getStringList("enabled-checks");
    }

    public ConfigurationSection getViolationHandling() {
        return this.config.getConfigurationSection("violation-handling");
    }

    public ConfigurationSection getCheck(Class<? extends Check> check) {
        return this.config.getConfigurationSection("checks." + check.getSimpleName().toLowerCase());
    }
    
}
