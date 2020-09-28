package com.github.ness.config;

import java.awt.Color;

import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class ColorSerialiser implements ValueSerialiser<Color> {

	@Override
	public Color deserialise(String key, Object value) throws BadValueException {
		try {
			return Color.decode(value.toString());
		} catch (NumberFormatException ex) {
			throw new BadValueException.Builder().key(key).cause(ex).build();
		}
	}

	@Override
	public Object serialise(Color value) {
		return String.format("#%02X%02X%02X", value.getRed(), value.getGreen(), value.getBlue());
	}

}
