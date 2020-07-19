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

public class WrapperPlayServerCraftProgressBar extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CRAFT_PROGRESS_BAR;
	
    /**
     * List of properties for furnaces.
     * 
     * @author Kristian
     */
    public static class FurnaceProperties {
    	/**
    	 * The value is then in the range 0 - 180.
    	 */
    	public static final int PROGRESS_ARROW = 0;
    	
    	/**
    	 * The value is in the range 0 - 250.
    	 */
    	public static final int PROGRESS_FIRE_ICON = 1;
    	
    	private static FurnaceProperties INSTANCE = new FurnaceProperties();
    	
    	public static FurnaceProperties getInstace() {
    		return INSTANCE;
    	}
    }
    
    public WrapperPlayServerCraftProgressBar() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerCraftProgressBar(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the id of the window to update.
     * @return The current Window id
    */
    public byte getWindowId() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the id of the window to update.
     * @param value - new value.
    */
    public void setWindowId(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve which property should be updated.
     * <p>
     * For the enchantment table, this is the slot ID.
     * @see {@link FurnaceProperties}
     * @return The current Property
    */
    public short getProperty() {
        return handle.getIntegers().read(1).shortValue();
    }
    
    /**
     * Set which property should be updated.
     * <p>
     * For the enchantment table, this is the slot ID.
     * @see {@link FurnaceProperties}
     * @param value - new value.
    */
    public void setProperty(short value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve the new value for the property.
     * <p>
     * For the enchantment table, this is the enchanting level in the given slot.
     * @return The current Value
    */
    public short getValue() {
        return handle.getIntegers().read(2).shortValue();
    }
    
    /**
     * Set the new value for the property.
     * <p>
     * For the enchantment table, this is the enchanting level in the given slot.
     * @param value - new value.
    */
    public void setValue(short value) {
        handle.getIntegers().write(2, (int) value);
    }
}

