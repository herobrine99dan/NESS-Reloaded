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

public class WrapperPlayServerEntityVelocity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_VELOCITY;
    
    public WrapperPlayServerEntityVelocity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityVelocity(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the entity ID.
     * @return The current Entity ID
    */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Retrieve the entity that will have its velocity updated.
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity that will have its velocity updated.
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
    	return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Set the entity ID.
     * @param value - new value.
    */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the velocity in the x axis.
     * @return The current velocity X
    */
    public double getVelocityX() {
        return handle.getIntegers().read(1) / 8000.0D;
    }
    
    /**
     * Set the velocity in the x axis.
     * @param value - new value.
    */
    public void setVelocityX(double value) {
        handle.getIntegers().write(1, (int) (value * 8000.0D));
    }
    
    /**
     * Retrieve the velocity in the y axis.
     * @return The current velocity y
    */
    public double getVelocityY() {
        return handle.getIntegers().read(2) / 8000.0D;
    }
    
    /**
     * Set the velocity in the y axis.
     * @param value - new value.
    */
    public void setVelocityY(double value) {
        handle.getIntegers().write(2, (int) (value * 8000.0D));
    }
    
    /**
     * Retrieve the velocity in the z axis.
     * @return The current velocity z
    */
    public double getVelocityZ() {
        return handle.getIntegers().read(3) / 8000.0D;
    }
    
    /**
     * Set the velocity in the z axis.
     * @param value - new value.
    */
    public void setVelocityZ(double value) {
        handle.getIntegers().write(3, (int) (value * 8000.0D));
    }
}

