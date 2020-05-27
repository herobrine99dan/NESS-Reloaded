package com.github.ness.packetswrapper;

import com.github.ness.nms.ReflectionUtility;

public class PacketPlayInPositionLook
extends SimplePacket {
    private double x;
    private double y;
    private double z;
    private boolean h;
    private float yaw;
    private float pitch;
    

    public PacketPlayInPositionLook(Object object) {
        super(object);
        try {
            this.x = (Double)ReflectionUtility.getDeclaredField(object, "a");
            this.y = (Double)ReflectionUtility.getDeclaredField(object, "b");
            this.z = (Double)ReflectionUtility.getDeclaredField(object, "c");
            this.yaw = (Float)ReflectionUtility.getDeclaredField(object, "d");
            this.pitch = (Float)ReflectionUtility.getDeclaredField(object, "e");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
    
    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

}

