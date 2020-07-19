package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerRelEntityMove extends WrapperPlayServerEntity {
    public static final PacketType TYPE = PacketType.Play.Server.REL_ENTITY_MOVE;
    
    public WrapperPlayServerRelEntityMove() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerRelEntityMove(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the relative movement in the x axis.
     * <p>
     * Note that this cannot exceed 4 blocks in either direction.
     * @return The current dX
    */
    public double getDx() {
        return handle.getBytes().read(0) / 32.0D;
    }
    
    /**
     * Set the relative movement in the x axis.
     * <p>
     * Note that this cannot exceed 4 blocks in either direction.
     * @param value - new value.
    */
    public void setDx(double value) {
    	if (Math.abs(value) > 4)
    		throw new IllegalArgumentException("Displacement cannot exceed 4 meters.");
        handle.getBytes().write(0, (byte) Math.min(Math.floor(value * 32.0D), 127));
    }
    
    /**
     * Retrieve the relative movement in the y axis.
     * <p>
     * Note that this cannot exceed 4 blocks in either direction.
     * @return The current dY
    */
    public double getDy() {
        return handle.getBytes().read(1) / 32.0D;
    }
    
    /**
     * Set the relative movement in the y axis.
     * <p>
     * Note that this cannot exceed 4 blocks in either direction.
     * @param value - new value.
    */
    public void setDy(double value) {
    	if (Math.abs(value) > 4)
    		throw new IllegalArgumentException("Displacement cannot exceed 4 meters.");
        handle.getBytes().write(1, (byte) Math.min(Math.floor(value * 32.0D), 127));
    }
    
    /**
     * Retrieve the relative movement in the z axis.
     * <p>
     * Note that this cannot exceed 4 blocks in either direction.
     * @return The current dZ
    */
    public double getDz() {
        return handle.getBytes().read(2) / 32.0D;
    }
    
    /**
     * Set the relative movement in the z axis.
     * <p>
     * Note that this cannot exceed 4 blocks in either direction.
     * @param value - new value.
    */
    public void setDz(double value) {
    	if (Math.abs(value) > 4)
    		throw new IllegalArgumentException("Displacement cannot exceed 4 meters.");
        handle.getBytes().write(2, (byte) Math.min(Math.floor(value * 32.0D), 127));
    }
}