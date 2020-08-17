package com.github.ness.packets.wrappers;

import com.github.ness.utility.ReflectionUtility;

public class PacketPlayInUseEntity
extends SimplePacket {
    private int id;
    private String action;

    public PacketPlayInUseEntity(Object object) {
        super(object);
        try {
            this.id = (Integer)ReflectionUtility.getDeclaredField(object, "a");
            this.action = ReflectionUtility.getDeclaredField(object, "action").toString();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getEntityId() {
        return this.id;
    }

    public String getAction() {
        return this.action;
    }
}

