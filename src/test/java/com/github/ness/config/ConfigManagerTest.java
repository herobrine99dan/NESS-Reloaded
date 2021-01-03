package com.github.ness.config;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Disabled("#117 - Static state breaks testing")
public class ConfigManagerTest {

	@TempDir
	public Path folder;

	private ConfigManager configManager;

	@BeforeEach
	public void setup() {
		configManager = new ConfigManager(folder);
	}
	
	@Test
	public void loadAndSaveDefaults() {
		assertDoesNotThrow(() -> configManager.reload().join());
	}

	@Test
	public void reloadUsingDefaults() {
		configManager.reload().join();
		assertDoesNotThrow(() -> configManager.reload().join());
	}
	
	
}
