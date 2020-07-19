package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientSteerVehicle extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.STEER_VEHICLE;
    
    public WrapperPlayClientSteerVehicle() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientSteerVehicle(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve positive to the left of the player.
     * @return The current Sideways
    */
    public float getSideways() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set positive to the left of the player.
     * @param value - new value.
    */
    public void setSideways(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve positive forward.
     * @return The current Forward
    */
    public float getForward() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set positive forward.
     * @param value - new value.
    */
    public void setForward(float value) {
        handle.getFloat().write(1, value);
    }
    
    /**
     * Retrieve whether or not the mounted player is jumping.
     * @return The current Jump
    */
    public boolean getJump() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set whether or not the mounted player is jumping.
     * @param value - new value.
    */
    public void setJump(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, (boolean) value);
    }
    
    /**
     * Retrieve true when leaving the vehicle.
     * @return The current Unmount
    */
    public boolean getUnmount() {
        return handle.getSpecificModifier(boolean.class).read(1);
    }
    
    /**
     * Set true when leaving the vehicle.
     * @param value - new value.
    */
    public void setUnmount(boolean value) {
        handle.getSpecificModifier(boolean.class).write(1, value);
    }
}