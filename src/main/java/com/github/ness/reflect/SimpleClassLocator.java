package com.github.ness.reflect;

import org.bukkit.Bukkit;

public final class SimpleClassLocator implements ClassLocator {

	private final String nmsVersion;

	private SimpleClassLocator(String nmsVersion) {
		this.nmsVersion = nmsVersion;
	}

	public static ClassLocator create() {
		return create(Bukkit.getServer().getClass().getPackage().getName());
	}

	static SimpleClassLocator create(String packageName) {
		return new SimpleClassLocator(packageName.substring(packageName.lastIndexOf(".") + 1));
	}

	String nmsVersion() {
		return nmsVersion;
	}

	public Class<?> getNmsClass(String className) {
		return classForName(getNmsClassName(className));
	}

	String getNmsClassName(String className) {
		return formatWithNms("net.minecraft.server", className);
	}

	public Class<?> getObcClass(String className) {
		return classForName(getObcClassName(className));
	}

	String getObcClassName(String className) {
		return formatWithNms("org.bukkit.craftbukkit", className);
	}

	private String formatWithNms(String packageName, String className) {
		return packageName + "." + nmsVersion + "." + className;
	}

	private Class<?> classForName(String clazzName) {
		try {
			return Class.forName(clazzName);
		} catch (ClassNotFoundException ex) {
			throw new ReflectionException(ex);
		}
	}

}
