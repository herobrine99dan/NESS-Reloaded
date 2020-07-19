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

public class WrapperPlayClientLook extends WrapperPlayClientFlying {
    public static final PacketType TYPE = PacketType.Play.Client.LOOK;
    
    public WrapperPlayClientLook() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientLook(PacketContainer packet) {
        super(packet, TYPE);
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
}


