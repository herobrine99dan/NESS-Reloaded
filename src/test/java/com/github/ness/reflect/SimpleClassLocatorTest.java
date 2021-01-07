package com.github.ness.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SimpleClassLocatorTest {

	private String nmsVersion(String packageName) {
		return SimpleClassLocator.create(packageName).nmsVersion();
	}

	@Test
	public void checkNmsVersion() {
		assertEquals(nmsVersion("org.bukkit.craftbukkit.v1_8_R3"), "v1_8_R3");
		assertEquals(nmsVersion("org.bukkit.craftbukkit.v1_16_R3"), "v1_16_R3");
	}

	@Test
	public void formatClassName() {
		SimpleClassLocator locator12 = SimpleClassLocator.create("org.bukkit.craftbukkit.v1_12_R1");
		assertEquals(
				"net.minecraft.server.v1_12_R1.EntityPlayer",
				locator12.getNmsClassName("EntityPlayer"));
		assertEquals(
				"org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer",
				locator12.getObcClassName("entity.CraftPlayer"));
	}

}
