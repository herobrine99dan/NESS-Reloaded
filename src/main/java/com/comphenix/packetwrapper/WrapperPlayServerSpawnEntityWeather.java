/*
 *  PacketWrapper - Contains wrappers for each packet in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerSpawnEntityWeather extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_WEATHER;
    
    public WrapperPlayServerSpawnEntityWeather() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerSpawnEntityWeather(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the entity ID of the thunderbolt.
     * @return The current Entity ID
    */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the entity ID of the thunderbolt.
     * @param value - new value.
    */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the global entity type.
     * <p>
     * Currently always 1 for thunderbolt.
     * @return The current Type
    */
    public byte getType() {
        return handle.getIntegers().read(4).byteValue();
    }
    
    /**
     * Set the global entity type.
     * <p>
     * Currently always 1 for thunderbolt.
     * @param value - new value.
    */
    public void setType(byte value) {
        handle.getIntegers().write(4, (int) value);
    }
    
    /**
     * Retrieve the x coordinate of the thunderbolt.
     * @return The current X
    */
    public double getX() {
        return handle.getIntegers().read(1) / 32.0D;
    }
    
    /**
     * Set the x coordinate of the thunderbolt.
     * @param value - new value.
    */
    public void setX(double value) {
        handle.getIntegers().write(1, (int) (value * 32.0D));
    }
    
    /**
     * Retrieve the y coordinate of the thunderbolt.
     * @return The current y
    */
    public double getY() {
        return handle.getIntegers().read(2) / 32.0D;
    }
    
    /**
     * Set the y coordinate of the thunderbolt.
     * @param value - new value.
    */
    public void setY(double value) {
        handle.getIntegers().write(2, (int) (value * 32.0D));
    }
    
    /**
     * Retrieve the z coordinate of the thunderbolt.
     * @return The current z
    */
    public double getZ() {
        return handle.getIntegers().read(3) / 32.0D;
    }
    
    /**
     * Set the z coordinate of the thunderbolt.
     * @param value - new value.
    */
    public void setZ(double value) {
        handle.getIntegers().write(3, (int) (value * 32.0D));
    }
}