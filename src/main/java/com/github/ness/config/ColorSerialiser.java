package com.github.ness.config;

import java.awt.Color;

import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class ColorSerialiser implements ValueSerialiser<Color> {

	@Override
	public Color deserialise(FlexibleType flexibleType) throws BadValueException {
		try {
			return Color.decode(flexibleType.getString());
		} catch (NumberFormatException ex) {
			throw flexibleType.badValueExceptionBuilder().cause(ex).build();
		}
	}

	@Override
	public Object serialise(Color value, Decomposer decomposer) {
		return String.format("#%02X%02X%02X", value.getRed(), value.getGreen(), value.getBlue());
	}

	@Override
	public Class<Color> getTargetClass() {
		return Color.class;
	}

}
