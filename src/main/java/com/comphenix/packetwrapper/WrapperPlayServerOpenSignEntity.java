package com.comphenix.packetwrapper;

import org.bukkit.Location;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Sent by the server to trigger the edit sign UI.
 * @author Kristian
 */
public class WrapperPlayServerOpenSignEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.OPEN_SIGN_ENTITY;
    
    public WrapperPlayServerOpenSignEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerOpenSignEntity(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the x coordinate of the sign to edit.
     * @return The current X
    */
    public int getX() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the x coordinate of the sign to edit.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the y coordinate of the sign to edit.
     * @return The current Y
    */
    public int getY() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set the y coordinate of the sign to edit.
     * @param value - new value.
    */
    public void setY(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve the z coordinate of the sign to edit.
     * @return The current Z
    */
    public int getZ() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set the z coordinate of the sign to edit.
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve the location of the sign.
     * @param event - the parent event.
     * @return The location.
     */
    public Location getLocation(PacketEvent event) {
    	return new Location(event.getPlayer().getWorld(), getX(), getY(), getZ());
    }
    
    /**
     * Set the location of the sign.
     * @param loc - the new location.
     */
    public void setLocation(Location loc) {
    	setX(loc.getBlockX());
    	setY((byte) loc.getBlockY());
    	setZ(loc.getBlockZ());
    }
}