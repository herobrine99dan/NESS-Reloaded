package com.github.ness.packets.wrappers;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import lombok.Getter;

public class PacketPlayInPositionLook extends SimplePacket {
	@Getter
	private double x;
	@Getter
	private double y;
	@Getter
	private double z;

	public PacketPlayInPositionLook(Object object) {
		super(object);
		try {
			/*
			 * this.x = (Double) ReflectionUtility.getDeclaredField(object, "a"); this.y =
			 * (Double) ReflectionUtility.getDeclaredField(object, "b"); this.z = (Double)
			 * ReflectionUtility.getDeclaredField(object, "c"); this.yaw = (Float)
			 * ReflectionUtility.getDeclaredField(object, "d"); this.pitch = (Float)
			 * ReflectionUtility.getDeclaredField(object, "e");
			 */

			this.x = (Double) getMethodValue(object, "a");
			this.y = (Double) getMethodValue(object, "b");
			this.z = (Double) getMethodValue(object, "c");

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public double getMethodValue(Object clazz, String value) {
		try {
			if (Bukkit.getVersion().contains("1.8")) {
				Method m = clazz.getClass().getMethod(value);
				return (double) m.invoke(clazz);
			} else {
				Method m = clazz.getClass().getMethod(value, double.class);
				return (double) m.invoke(clazz, 0.0);
			}
		} catch (Exception ex) {
			return -101010.0;
		}
	}

}
