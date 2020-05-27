package com.github.ness.packetswrapper;

import com.github.ness.nms.ReflectionUtility;

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

