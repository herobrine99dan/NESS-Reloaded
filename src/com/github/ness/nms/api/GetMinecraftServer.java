package com.github.ness.nms.api;

import java.lang.reflect.Field;

public class GetMinecraftServer {

	private static final Field FIELD;

	static {
		try {
			FIELD = NMS.getNMSClass("MinecraftServer").getDeclaredField("recentTps");
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private GetMinecraftServer() {
	}

	static double[] invoke(Object object) throws IllegalArgumentException, IllegalAccessException {
		return (double[]) FIELD.get(object);
	}

}
