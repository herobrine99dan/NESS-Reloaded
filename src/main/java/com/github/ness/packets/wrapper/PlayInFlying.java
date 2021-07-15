package com.github.ness.packets.wrapper;

import com.github.ness.data.ImmutableLoc;
import com.github.ness.packets.PacketType;
import com.github.ness.reflect.FieldInvoker;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.ReflectHelper;

public final class PlayInFlying {

	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	private final boolean onGround;
	private final boolean hasPosition;
	private final boolean hasLook;

	private PlayInFlying(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean hasPosition,
			boolean hasLook) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		this.hasPosition = hasPosition;
		this.hasLook = hasLook;
	}

	public static PacketType<PlayInFlying> type(ReflectHelper helper) {
		Class<?> packetClass = helper.getNmsClass("PacketPlayInFlying");

		FieldInvoker<Double> x = helper.getFieldFromNames(packetClass, double.class, "x", "a");
		FieldInvoker<Double> y = helper.getFieldFromNames(packetClass, double.class, "y", "b");
		FieldInvoker<Double> z = helper.getFieldFromNames(packetClass, double.class, "z","c");
		FieldInvoker<Float> yaw = helper.getFieldFromNames(packetClass, float.class, "yaw","d");
		FieldInvoker<Float> pitch = helper.getFieldFromNames(packetClass, float.class, "pitch","e");
		FieldInvoker<Boolean> onGround = helper.getFieldFromNames(packetClass,
				boolean.class, "onGround", "f"); // The field name is 'f' in some versions
		FieldInvoker<Boolean> hasPosition = helper.getFieldFromNames(packetClass, boolean.class, "hasPos","g");
		FieldInvoker<Boolean> hasLook = helper.getFieldFromNames(packetClass,boolean.class, "hasLook","h");

		return new RawPacketType<PlayInFlying>(packetClass) {

			@Override
			public PlayInFlying convertPacket(Object packet) {
				return new PlayInFlying(x.get(packet), y.get(packet), z.get(packet), yaw.get(packet), pitch.get(packet),
						onGround.get(packet), hasPosition.get(packet), hasLook.get(packet));
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

	public boolean onGround() {
		return onGround;
	}

	public boolean hasPosition() {
		return hasPosition;
	}

	public boolean hasLook() {
		return hasLook;
	}

	public ImmutableLoc toImmutableLoc() {
		return new ImmutableLoc("useless", x, y, z, yaw, pitch);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PlayInFlying that = (PlayInFlying) o;
		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0
				&& Float.compare(that.yaw, yaw) == 0 && Float.compare(that.pitch, pitch) == 0
				&& onGround == that.onGround && hasPosition == that.hasPosition && hasLook == that.hasLook;
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
		result = 31 * result + (onGround ? 1 : 0);
		result = 31 * result + (hasPosition ? 1 : 0);
		result = 31 * result + (hasLook ? 1 : 0);
		return result;
	}
}
