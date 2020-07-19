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

public class WrapperPlayClientWindowClick extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.WINDOW_CLICK;
    
    public WrapperPlayClientWindowClick() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientWindowClick(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the id of the window which was clicked. 
     * <p>
     * Use 0 for the player inventory.
     * @return The current Window id
    */
    public byte getWindowId() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the id of the window which was clicked. 
     * <p>
     * Use 0 for the player inventory.
     * @param value - new value.
    */
    public void setWindowId(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve the clicked slot index.
     * @return The current Slot
    */
    public short getSlot() {
        return handle.getIntegers().read(1).shortValue();
    }
    
    /**
     * Set the clicked slot index.
     * @param value - new value.
    */
    public void setSlot(short value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve the mouse button that was clicked.
     * <p>
     * Here zero is left click, one is right click and three is middle click.
     * @return The current Mouse button
    */
    public byte getMouseButton() {
        return handle.getIntegers().read(2).byteValue();
    }
    
    /**
     * Set the mouse button that was clicked.
     * <p>
     * Here zero is left click, one is right click and three is middle click.
     * @param value - new value.
    */
    public void setMouseButton(byte value) {
        handle.getIntegers().write(2, (int) value);
    }
    
    /**
     * Retrieve a unique number for the action, used for transaction handling (See the Transaction packet)..
     * @return The current Action number
    */
    public short getActionNumber() {
        return handle.getShorts().read(0);
    }
    
    /**
     * Set a unique number for the action, used for transaction handling (See the Transaction packet)..
     * @param value - new value.
    */
    public void setActionNumber(short value) {
        handle.getShorts().write(0, value);
    }
    
    /**
     * Retrieve the click mode.
     * <p>
     * See <a href="http://wiki.vg/Protocol#Click_Window">Click Window</a> for more details.
     * @return The current mode.
    */
    public int getMode() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set the click mode.
     * <p>
     * See <a href="http://wiki.vg/Protocol#Click_Window">Click Window</a> for more details.
     * @param value - new value.
    */
    public void setMode(int mode) {
        handle.getIntegers().write(3, mode);
    }
    
    /**
     * Retrieve the item that was clicked in the inventory.
     * @return The current Clicked item
    */
    public ItemStack getClickedItem() {
        return handle.getItemModifier().read(0);
    }
    
    /**
     * Set the item that was clicked in the inventory.
     * @param value - new value.
    */
    public void setClickedItem(ItemStack value) {
        handle.getItemModifier().write(0, value);
    }
}