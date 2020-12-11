package com.github.ness.reflect;

import org.bukkit.Bukkit;

public class ClassLocator {

	private final String nmsVersion;

	private ClassLocator(String nmsVersion) {
		this.nmsVersion = nmsVersion;
	}

	public static ClassLocator create() {
		return create(Bukkit.getServer().getClass().getPackage().getName());
	}

	static ClassLocator create(String packageName) {
		return new ClassLocator(packageName.substring(packageName.lastIndexOf(".") + 1));
	}

	String nmsVersion() {
		return nmsVersion;
	}

	private String addNmsVersion(String packageName) {
		return packageName + "." + nmsVersion + ".";
	}

	public Class<?> getNmsClass(String name) {
		return classForName(addNmsVersion("net.minecraft.server") + name);
	}

	public Class<?> getObcClass(String name) {
		return classForName(addNmsVersion("org.bukkit.craftbukkit") + name);
	}

	public Class<?> classForName(String clazzName) {
		try {
			return Class.forName(clazzName);
		} catch (ClassNotFoundException ex) {
			throw new ReflectionException(ex);
		}
	}

}
