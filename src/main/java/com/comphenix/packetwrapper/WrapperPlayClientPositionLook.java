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

public class WrapperPlayClientPositionLook extends WrapperPlayClientFlying {
	public static final PacketType TYPE = PacketType.Play.Client.POSITION_LOOK;
    
    public WrapperPlayClientPositionLook() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientPositionLook(PacketContainer packet) {
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
     * Retrieve used to modify the players bounding box when going up stairs, crouching, etc.
     * @return The current Stance
    */
    public double getStance() {
        return handle.getDoubles().read(3);
    }
    
    /**
     * Set used to modify the players bounding box when going up stairs, crouching, etc.
     * @param value - new value.
    */
    public void setStance(double value) {
        handle.getDoubles().write(3, value);
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
     * Retrieve absolute rotation on the X Axis.
     * @return The current Yaw
    */
    public float getYaw() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set absolute rotation on the X Axis.
     * @param value - new value.
    */
    public void setYaw(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve absolute rotation on the Y Axis.
     * @return The current Pitch
    */
    public float getPitch() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set absolute rotation on the Y Axis.
     * @param value - new value.
    */
    public void setPitch(float value) {
        handle.getFloat().write(1, value);
    }
}