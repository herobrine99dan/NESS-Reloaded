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

public class WrapperPlayServerUpdateTime extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_TIME;
    
    public WrapperPlayServerUpdateTime() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerUpdateTime(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the age of the world in ticks. 
     * <p>
     * This cannot be changed by server commands.
     * @return The current age of the world
    */
    public long getAgeOfTheWorld() {
        return handle.getLongs().read(0);
    }
    
    /**
     * Set the age of the world in ticks.
     * <p>
     * This cannot be changed by server commands.
     * @param value - new value.
    */
    public void setAgeOfTheWorld(long value) {
        handle.getLongs().write(0, value);
    }
    
    /**
     * Retrieve the world (or region) time, in ticks.
     * @return The current Time of Day
    */
    public long getTimeOfDay() {
        return handle.getLongs().read(1);
    }
    
    /**
     * Set the world (or region) time, in ticks.
     * @param value - new value.
    */
    public void setTimeOfDay(long value) {
        handle.getLongs().write(1, value);
    }
}