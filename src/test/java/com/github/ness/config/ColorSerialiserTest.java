package com.github.ness.config;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import space.arim.dazzleconf.error.BadValueException;

public class ColorSerialiserTest {

	private static final Logger logger = LoggerFactory.getLogger(ColorSerialiserTest.class);
	
	@Test
	public void testSerialiseDeserialise() throws BadValueException {
		ColorSerialiser serialiser = new ColorSerialiser();
		Color colour = Color.RED;
		logger.info("RGB = {}, RGB-hex = {}", Integer.toString(colour.getRGB()), Integer.toHexString(colour.getRGB()));
		Object serialised = serialiser.serialise(colour);
		logger.info("Serialised = {}", serialised);
		serialiser.deserialise("testkey", serialised);
	}
	
}
