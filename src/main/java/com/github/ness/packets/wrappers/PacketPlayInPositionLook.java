package com.github.ness.packets.wrappers;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;

public class PacketPlayInPositionLook extends SimplePacket {
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

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

			this.x = (Double) getMethodValue(object,"a");
			this.y = (Double) getMethodValue(object, "b");
			this.z = (Double) getMethodValue(object, "c");
			this.yaw = (float) getMethodValue(object, "d");
			this.pitch = (float) getMethodValue(object, "e");

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getPitch() {
		return this.pitch;
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
