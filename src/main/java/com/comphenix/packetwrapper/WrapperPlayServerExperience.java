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

public class WrapperPlayServerExperience extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.EXPERIENCE;
    
    public WrapperPlayServerExperience() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerExperience(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the new amount of experience in the experience bar as a value between 0 and 1.
     * @return The current Experience bar
    */
    public float getExperienceBar() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set the new amount of experience in the experience bar as a value between 0 and 1.
     * @param value - new value.
    */
    public void setExperienceBar(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve the displayed level.
     * @return The current Level
    */
    public short getLevel() {
        return handle.getIntegers().read(1).shortValue();
    }
    
    /**
     * Set the displayed level.
     * @param value - new value.
    */
    public void setLevel(short value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve the total amount of experienced gained.
     * @return The current Total experience
    */
    public short getTotalExperience() {
        return handle.getIntegers().read(0).shortValue();
    }
    
    /**
     * Set the total amount of experience gained.
     * @param value - new value.
    */
    public void setTotalExperience(short value) {
        handle.getIntegers().write(0, (int) value);
    }
}


