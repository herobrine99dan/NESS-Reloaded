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

import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerSpawnPosition extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_POSITION;
    
    public WrapperPlayServerSpawnPosition() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerSpawnPosition(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the X coordinate of the spawn point.
     * @return The current x coordinate.
    */
    public int getX() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the X coordinate of the spawn point.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the Y coordinate of the spawn point.
     * @return The current Y
    */
    public int getY() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set the Y coordinate of the spawn point.
     * @param value - new value.
    */
    public void setY(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve the Z coordinate of the spawn point.
     * @return The current Z
    */
    public int getZ() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set the Z coordinate of the spawn point..
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Set the spawn location using a vector.
     * @param point - the new spawn location.
     */
    public void setLocation(Vector point) {
    	setX(point.getBlockX());
    	setY(point.getBlockY());
    	setZ(point.getBlockZ());
    }
    
    /**
     * Retrieve the spawn location as a vector.
     * @return The spawn location.
     */
    public Vector getLocation() {
    	return new Vector(getX(), getY(), getZ());
    }
}