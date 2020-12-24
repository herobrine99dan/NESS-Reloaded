package com.github.ness.packets.wrapper;

import com.github.ness.packets.Packet;
import com.github.ness.packets.PacketType;
import com.github.ness.reflect.ReflectHelper;

public class PlayInFlying {

	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;

	private PlayInFlying(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	static PacketType<PlayInFlying> type(ReflectHelper helper) {
		Class<?> nmsClass = helper.getNmsClass("PacketPlayInFlying");
		return new PacketType<PlayInFlying>() {
			@Override
			public boolean isPacket(Packet packet) {
				return nmsClass.isInstance(packet.getRawPacket());
			}

			@Override
			public PlayInFlying convertPacket(Packet packet) {
				return new PlayInFlying(
						packet.invokeField(double.class, "x"),
						packet.invokeField(double.class, "y"),
						packet.invokeField(double.class, "z"),
						packet.invokeField(float.class, "yaw"),
						packet.invokeField(float.class, "pitch"));
			}
		};
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public double z() {
		return z;
	}

	public float yaw() {
		return yaw;
	}

	public float pitch() {
		return pitch;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayInFlying that = (PlayInFlying) o;
		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0
				&& Float.compare(that.yaw, yaw) == 0 && Float.compare(that.pitch, pitch) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
		result = 31 * result + (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
		return result;
	}
}
