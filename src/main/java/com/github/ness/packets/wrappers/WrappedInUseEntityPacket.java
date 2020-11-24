
package com.github.ness.packets.wrappers;

import java.lang.reflect.Field;

import lombok.Getter;

public class WrappedInUseEntityPacket extends SimplePacket {

    private static Field fieldId;
    private static Field fieldAction;

    @Getter
    private int entityId;
    @Getter
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

    @Override
    public void process() throws IllegalArgumentException, IllegalAccessException {
        entityId = fieldId.getInt(this.getPacket());
        action = EnumEntityUseAction.values()[((Enum<?>) fieldAction.get(this.getPacket())).ordinal()];
    }

    public enum EnumEntityUseAction {
        INTERACT, ATTACK, INTERACT_AT
    }
}