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

public class WrapperPlayServerHeldItemSlot extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.HELD_ITEM_SLOT;
    
    public WrapperPlayServerHeldItemSlot() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerHeldItemSlot(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the slot which the player has selected (0-8).
     * @return The current Slot ID
    */
    public short getSlotId() {
        return handle.getIntegers().read(0).shortValue();
    }
    
    /**
     * Set the slot which the player has selected (0-8).
     * @param value - new value.
    */
    public void setSlotId(short value) {
        handle.getIntegers().write(0, (int) value);
    }
}

