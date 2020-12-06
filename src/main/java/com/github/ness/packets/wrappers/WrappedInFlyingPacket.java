package com.github.ness.packets.wrappers;

import java.lang.reflect.Field;

public class WrappedInFlyingPacket extends SimplePacket {

	private static Field fieldX;
	private static Field fieldY;
	private static Field fieldZ;
	private static Field fieldYaw;
	private static Field fieldPitch;
	private static Field fieldGround;
	private static volatile boolean initialized = false;

	private double x, y, z;
	private float yaw, pitch;
	private boolean look, pos, onGround;

	public WrappedInFlyingPacket(Object packet) throws IllegalArgumentException {
		super(packet);
		if (!initialized) {
			fieldX = getField(packet.getClass(), double.class, 0);
			fieldY = getField(packet.getClass(), double.class, 1);
			fieldZ = getField(packet.getClass(), double.class, 2);
			fieldYaw = getField(packet.getClass(), float.class, 0);
			fieldPitch = getField(packet.getClass(), float.class, 1);
			fieldGround = getField(packet.getClass(), boolean.class, 0);
			initialized = true;
		}
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public boolean isLook() {
		return look;
	}

	public boolean isPos() {
		return pos;
	}

	public boolean isOnGround() {
		return onGround;
	}

	@Override
	public void process() throws IllegalArgumentException, IllegalAccessException {
		String name = this.getName().toLowerCase();
		pos = name.contains("position");
		look = name.contains("look");
		if (pos) {
			x = fieldX.getDouble(this.getPacket());
			y = fieldY.getDouble(this.getPacket());
			z = fieldZ.getDouble(this.getPacket());
		}
		if (look) {
			yaw = fieldYaw.getFloat(this.getPacket());
			pitch = fieldPitch.getFloat(this.getPacket());
		}
		onGround = fieldGround.getBoolean(this.getPacket());
	}

}
