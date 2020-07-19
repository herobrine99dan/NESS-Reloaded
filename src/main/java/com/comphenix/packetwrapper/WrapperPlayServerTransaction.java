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

public class WrapperPlayServerTransaction extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.TRANSACTION;
    
    public WrapperPlayServerTransaction() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerTransaction(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the id of the window that the action occurred in.
     * @return The current Window id
    */
    public byte getWindowId() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the id of the window that the action occurred in..
     * @param value - new value.
    */
    public void setWindowId(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve every action that is to be accepted has a unique number. 
     * <p>
     * This field corresponds to that number..
     * @return The current Action number
    */
    public short getActionNumber() {
        return handle.getShorts().read(0);
    }
    
    /**
     * Set every action that is to be accepted has a unique number. 
     * <p>
     * This field corresponds to that number.
     * @param value - new value.
    */
    public void setActionNumber(short value) {
        handle.getShorts().write(0, value);
    }
    
    /**
     * Retrieve whether or not the action was accepted.
     * @return The current Accepted?
    */
    public boolean getAccepted() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set whether or not the action was accepted.
     * @param value - new value.
    */
    public void setAccepted(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
}
