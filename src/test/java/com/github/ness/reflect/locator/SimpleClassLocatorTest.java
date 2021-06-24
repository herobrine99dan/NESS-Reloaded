package com.github.ness.reflect.locator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleClassLocatorTest {

	@Test
	public void formatClassName() {
		SimpleClassLocator locator12 = new SimpleClassLocator("v1_12_R1");
		assertEquals(
				"net.minecraft.server.v1_12_R1.EntityPlayer",
				locator12.getNmsClassName("EntityPlayer"));
		assertEquals(
				"org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer",
				locator12.getObcClassName("entity.CraftPlayer"));
	}

}
