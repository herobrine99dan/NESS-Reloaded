package com.github.ness.packetswrapper;

import com.github.ness.nms.ReflectionUtility;

public class PacketPlayInFlying
extends SimplePacket {
    private boolean onGround;

    public PacketPlayInFlying(Object object) {
        super(object);
        try {
            this.onGround = (Boolean) ReflectionUtility.getField(object, "f");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean isOnGround() {
        return this.onGround;
    }
}

