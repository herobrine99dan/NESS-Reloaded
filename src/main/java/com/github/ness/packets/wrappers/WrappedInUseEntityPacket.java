
package com.github.ness.packets.wrappers;

import java.lang.reflect.Field;

public class WrappedInUseEntityPacket extends SimplePacket {

	private static Field fieldId;
	private static Field fieldAction;

	private int entityId;
	private EnumEntityUseAction action;
	private static volatile boolean initialized = false;

	public WrappedInUseEntityPacket(Object packet) {
		super(packet);
		if (!initialized) {
			fieldId = this.getField(packet.getClass(), int.class, 0);
			fieldAction = getField(packet.getClass(), Enum.class, 0);
			initialized = true;
		}
	}

	public int getEntityId() {
		return entityId;
	}

	public EnumEntityUseAction getAction() {
		return action;
	}

	@Override
	public void process() throws IllegalArgumentException, IllegalAccessException {
		entityId = fieldId.getInt(this.getPacket());
		action = EnumEntityUseAction.values()[((Enum<?>) fieldAction.get(this.getPacket())).ordinal()];
	}

	public enum EnumEntityUseAction {
		INTERACT, ATTACK, INTERACT_AT
	}
}