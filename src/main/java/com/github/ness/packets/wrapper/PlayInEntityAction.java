package com.github.ness.packets.wrapper;

import java.util.Objects;

import com.github.ness.packets.PacketType;
import com.github.ness.reflect.FieldInvoker;
import com.github.ness.reflect.ReflectHelper;
import com.github.ness.reflect.locator.VersionDetermination;

public class PlayInEntityAction {

	private final Enum animation;

	private PlayInEntityAction(Object animation) {
		this.animation = (Enum) animation;
	}

	public static PacketType<PlayInEntityAction> type(VersionDetermination version, ReflectHelper helper) {
		Class<?> packetClass = helper.getNmsClass("PacketPlayInEntityAction");
		Class<?> enumPlayerActionClass = helper.getNmsClass("PacketPlayInEntityAction$EnumPlayerAction");
		FieldInvoker<?> animation = helper.getFieldFromNames(packetClass, enumPlayerActionClass, "b", "animation");

		return new RawPacketType<PlayInEntityAction>(packetClass) {

			@Override
			public PlayInEntityAction convertPacket(Object packet) {
				return new PlayInEntityAction(animation.get(packet));
			}
		};
	}

	public Enum animation() {
		return animation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(animation);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayInEntityAction other = (PlayInEntityAction) obj;
		return Objects.equals(animation, other.animation);
	}

}
