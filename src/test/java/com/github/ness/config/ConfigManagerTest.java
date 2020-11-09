package com.github.ness.config;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConfigManagerTest {

	@TempDir
	public Path folder;
	
	@Test
	public void testReload() {
		//new ConfigManager(folder).reload().join();
	}
	
	
}
