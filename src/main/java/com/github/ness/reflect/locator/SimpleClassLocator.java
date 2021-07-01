package com.github.ness.reflect.locator;

import com.github.ness.reflect.ReflectionException;

final class SimpleClassLocator implements ClassLocator {

	private final String nmsVersion;

	SimpleClassLocator(String nmsVersion) {
		this.nmsVersion = nmsVersion;
	}

	@Override
	public Class<?> getNmsClass(String className) {
		return classForName(getNmsClassName(className));
	}

	String getNmsClassName(String className) {
		return formatWithNms("net.minecraft.server", className);
	}

	@Override
	public Class<?> getObcClass(String className) {
		return classForName(getObcClassName(className));
	}

	String getObcClassName(String className) {
		return formatWithNms("org.bukkit.craftbukkit", className);
	}

	private String formatWithNms(String packageName, String className) {
		return packageName + '.' + nmsVersion + '.' + className;
	}

	private Class<?> classForName(String clazzName) {
		try {
			return Class.forName(clazzName);
		} catch (ClassNotFoundException ex) {
			throw new ReflectionException(ex);
		}
	}

}
