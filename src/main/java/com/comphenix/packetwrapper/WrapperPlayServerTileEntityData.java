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
import com.comphenix.protocol.wrappers.nbt.NbtBase;

public class WrapperPlayServerTileEntityData extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.TILE_ENTITY_DATA;
    
    public WrapperPlayServerTileEntityData() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerTileEntityData(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the x coordinate of the block associated with this tile entity.
     * @return The current X
    */
    public int getX() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the x coordinate of the block associated with this tile entity.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the y coordinate of the block associated with this tile entity.
     * @return The current Y
    */
    public short getY() {
        return handle.getIntegers().read(1).shortValue();
    }
    
    /**
     * Set the y coordinate of the block associated with this tile entity.
     * @param value - new value.
    */
    public void setY(short value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve the z coordinate of the block associated with this tile entity.
     * @return The current Z
    */
    public int getZ() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set the z coordinate of the block associated with this tile entity.
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve the type of update to perform.
     * @return The current Action
    */
    public byte getAction() {
        return handle.getIntegers().read(3).byteValue();
    }
    
    /**
     * Set the type of update to perform.
     * @param value - new value.
    */
    public void setAction(byte value) {
        handle.getIntegers().write(3, (int) value);
    }
    
    /**
     * Retrieve the NBT data of the current tile entity.
     * @return The current tile entity.
    */
    public NbtBase<?> getNbtData() {
        return handle.getNbtModifier().read(0);
    }
    
    /**
     * Set the NBT data of the current tile entity.
     * @param value - new value.
    */
    public void setNbtData(NbtBase<?> value) {
        handle.getNbtModifier().write(0, value);
    }
}