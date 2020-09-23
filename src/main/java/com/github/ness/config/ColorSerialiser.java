package com.github.ness.config;

import java.awt.Color;
import java.util.Locale;
import java.util.logging.Logger;

import com.github.ness.NessLogger;

import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class ColorSerialiser implements ValueSerialiser<Color> {

	private static final Logger logger = NessLogger.getLogger(ColorSerialiser.class);
	
	@Override
	public Color deserialise(String key, Object value) throws BadValueException {
		if (value instanceof Number) {
			return new Color(((Number) value).intValue());
		}
		try {
			Object colourObj = Color.class.getField(value.toString().toUpperCase(Locale.ROOT)).get(null);
			if (colourObj instanceof Color) {
				logger.info("It is advised to change the colour at " + key + " to integer format");
				return (Color) colourObj;
			}
		} catch (IllegalAccessException | NoSuchFieldException ignored) {
			
		}
		try {
			return Color.decode(value.toString());
		} catch (NumberFormatException ex) {
			throw new BadValueException.Builder().key(key).cause(ex).build();
		}
	}

	@Override
	public Object serialise(Color value) {
		return value.getRGB();
	}

}
