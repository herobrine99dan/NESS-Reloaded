package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerPosition extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.POSITION;
    
    public WrapperPlayServerPosition() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerPosition(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve absolute position.
     * @return The current X
    */
    public double getX() {
        return handle.getDoubles().read(0);
    }
    
    /**
     * Set absolute position.
     * @param value - new value.
    */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }
    
    /**
     * Retrieve absolute position.
     * @return The current Y
    */
    public double getY() {
        return handle.getDoubles().read(1);
    }
    
    /**
     * Set absolute position.
     * @param value - new value.
    */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }
    
    /**
     * Retrieve absolute position.
     * @return The current Z
    */
    public double getZ() {
        return handle.getDoubles().read(2);
    }
    
    /**
     * Set absolute position.
     * @param value - new value.
    */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }
    
    /**
     * Retrieve absolute rotation on the X Axis, in degrees.
     * @return The current Yaw
    */
    public float getYaw() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set absolute rotation on the X Axis, in degrees.
     * @param value - new value.
    */
    public void setYaw(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve absolute rotation on the Y Axis, in degrees.
     * @return The current Pitch
    */
    public float getPitch() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set absolute rotation on the Y Axis, in degrees.
     * @param value - new value.
    */
    public void setPitch(float value) {
        handle.getFloat().write(1, value);
    }
    
    /**
     * Retrieve true if the client is on the ground, False otherwise.
     * @return The current On Ground
    */
    public boolean getOnGround() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set true if the client is on the ground, False otherwise.
     * @param value - new value.
    */
    public void setOnGround(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
}