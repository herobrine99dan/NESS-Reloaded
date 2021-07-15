package com.github.ness.packets.wrapper;

import java.lang.reflect.Field;
import java.util.Objects;

import com.github.ness.packets.PacketType;
import com.github.ness.reflect.FieldInvoker;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.ReflectHelper;

public class PlayInEntityAction {

	private final Enum animation;

	private PlayInEntityAction(Object animation) {
		this.animation = (Enum) animation;
	}

	public static PacketType<PlayInEntityAction> type(ReflectHelper helper) {
		Class<?> packetClass = helper.getNmsClass("PacketPlayInEntityAction");
		//For now this will remain here for testing purposes
		for (Field field : packetClass.getDeclaredFields()) {
			System.out.println("name: " + field.getName() + " type: " + field.getType().getSimpleName());
		}
		FieldInvoker<?> animation = helper.getFieldFromNames(packetClass, Object.class, "b", "animation");

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
