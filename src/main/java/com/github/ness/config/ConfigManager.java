package com.github.ness.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ness.NessLogger;

import space.arim.dazzleconf.AuxiliaryKeys;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;

public class ConfigManager {

	private final Path configPath;
	private final Path messagesPath;

	private final ConfigurationFactory<NessConfig> configFactory;
	private final ConfigurationFactory<NessMessages> messagesFactory;
	
	private volatile Configs configs;
	
	private static final Logger logger = NessLogger.getLogger(ConfigManager.class);
	
	public ConfigManager(Path folder) {
		if (!Files.isDirectory(folder)) {
			try {
				Files.createDirectories(folder);
			} catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}
		configPath = folder.resolve("config.yml");
		messagesPath = folder.resolve("messages.yml");

		ConfigurationOptions configOptions = new ConfigurationOptions.Builder()
				.addSerialiser(java.awt.Color.class, new ColorSerialiser()).build();
		SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder().useCommentingWriter(true).build();
		configFactory = new SnakeYamlConfigurationFactory<>(NessConfig.class, configOptions, yamlOptions);
		messagesFactory = new SnakeYamlConfigurationFactory<>(NessMessages.class, configOptions, yamlOptions);
	}
	
	/**
	 * Gets the config.yml configuration
	 * 
	 * @return the main config
	 */
	public NessConfig getConfig() {
		return configs.config;
	}
	
	/**
	 * Gets the messages.yml configuration
	 * 
	 * @return the messages config
	 */
	public NessMessages getMessages() {
		return configs.messages;
	}
	
	private <C> CompletableFuture<C> loadConf(ConfigurationFactory<C> factory, Path path) {
		return CompletableFuture.supplyAsync(() -> {

			C defaults = factory.loadDefaults();

			if (Files.exists(path)) {

				C config;
				try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {

					config = factory.load(fileChannel, defaults);
				} catch (IOException ex) {
					throw new UncheckedIOException("Cannot read config from file", ex);

				} catch (ConfigFormatSyntaxException ex) {
					logger.log(Level.WARNING,
							"The YAML syntax in your configuration is invalid. "
							+ "Please use a yaml syntax checker such as https://yaml-online-parser.appspot.com/ . "
							+ "Paste your configuration there and use it to work through errors. "
							+ "For now, NESS will use the default configuration.", ex);
					return defaults;

				} catch (InvalidConfigException ex) {
					logger.log(Level.WARNING,
							"The values in your configuration are invalid. "
							+ "Please correct the issue and run /ness reload. "
							+ "For now, NESS will use the default configuration.", ex);
					return defaults;
				}
				if (config instanceof AuxiliaryKeys) {
					try (FileChannel fileChannel = FileChannel.open(path,
							StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {

						factory.write(config, fileChannel);
					} catch (IOException ex) {
						throw new UncheckedIOException("Unable to update configuration", ex);
					}
				}
				return config;

			} else {
				try (FileChannel fileChannel = FileChannel.open(path,
						StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {

					factory.write(defaults, fileChannel);
				} catch (IOException ex) {
					throw new UncheckedIOException("Cannot write default config to file", ex);
				}
				return defaults;
			}
		});
	}
	
	public CompletableFuture<?> reload() {
		CompletableFuture<NessConfig> futureConfig = loadConf(configFactory, configPath);
		CompletableFuture<NessMessages> futureMessages = loadConf(messagesFactory, messagesPath);

		return CompletableFuture.allOf(futureConfig, futureMessages).thenRun(() -> {
			this.configs = new Configs(futureConfig.join(), futureMessages.join());
			logger.info("Loaded config.yml and messages.yml");
		});
	}
	
	private static class Configs {
		
		final NessConfig config;
		final NessMessages messages;
		
		Configs(NessConfig config, NessMessages messages) {
			this.config = config;
			this.messages = messages;
		}
		
	}
	
}
