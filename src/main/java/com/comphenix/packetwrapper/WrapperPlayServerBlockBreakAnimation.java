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

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class WrapperPlayServerBlockBreakAnimation extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_BREAK_ANIMATION;
    
    public WrapperPlayServerBlockBreakAnimation() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerBlockBreakAnimation(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the entity breaking the block.
     * @return The current EID?
    */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the entity breaking the block.
     * @param value - new value.
    */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the entity.
     * @param world - the current world of the entity.
     * @return The entity.
     */
    public Entity getEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity.
     * @param event - the packet event.
     * @return The entity.
     */
    public Entity getEntity(PacketEvent event) {
    	return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Retrieve the x axis of the block coordinate.
     * @return The current X
    */
    public int getX() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set the x axis of the block coordinate.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve the y axis of the block coordinate.
     * @return The current Y
    */
    public int getY() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set the y axis of the block coordinate.
     * @param value - new value.
    */
    public void setY(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve the z axis of the block coordinate.
     * @return The current Z
    */
    public int getZ() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set the z axis of the block coordinate.
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(3, value);
    }
    
    /**
     * Retrieve how far destroyed this block is (0 - 9).
     * @return The current Destroy Stage
    */
    public byte getDestroyStage() {
        return handle.getIntegers().read(4).byteValue();
    }
    
    /**
     * Set how far destroyed this block is (0 - 9).
     * @param value - new value.
    */
    public void setDestroyStage(byte value) {
        handle.getIntegers().write(4, (int) value);
    }
}

