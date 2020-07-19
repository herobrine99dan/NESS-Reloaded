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

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerWindowItems extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WINDOW_ITEMS;
    
    public WrapperPlayServerWindowItems() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerWindowItems(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the ID of the window which is being updated. 
     * <p>
     * Use 0 for the player inventory.
     * @return The current Window id
    */
    public byte getWindowId() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the ID of the window which is being updated. 
     * <p>
     * Use 0 for the player inventory.
     * @param value - new value.
    */
    public void setWindowId(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve the items in the inventory indexed by slot index.
     * @return The items that will fill the inventory.
    */
    public ItemStack[] getItems() {
        return handle.getItemArrayModifier().read(0);
    }
    
    /**
     * Set the items in the inventory indexed by slot index.
     * @param value - new value.
    */
    public void setItems(ItemStack[] value) {
        handle.getItemArrayModifier().write(0, value);
    }
}