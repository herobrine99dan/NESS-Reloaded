package com.github.ness.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ClassLocatorTest {

	private String nmsVersion(String packageName) {
		return ClassLocator.create(packageName).nmsVersion();
	}

	@Test
	public void testNmsVersion() {
		assertEquals(nmsVersion("org.bukkit.craftbukkit.v1_8_R3"), "v1_8_R3");
		assertEquals(nmsVersion("org.bukkit.craftbukkit.v1_16_R3"), "v1_16_R3");
	}

}
