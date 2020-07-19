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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * This packet tells that a player goes to bed. 
 * @author Kristian
 */
public class WrapperPlayServerBed extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.BED;
    
    public WrapperPlayServerBed() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerBed(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve player ID.
     * @return The current Entity ID
    */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set player ID.
     * @param value - new value.
    */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the player's entity object.
     * @param world - the word the player has joined.
     * @return The player's entity.
     */
    public Entity getEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the player's entity object.
     * @param event - the packet event.
     * @return The player's entity.
     */
    public Entity getEntity(PacketEvent event) {
    	return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Retrieve bed headboard X as block coordinate.
     * @return The current X coordinate
    */
    public int getX() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set bed headboard X as block coordinate.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve bed headboard Y as block coordinate.
     * @return The current Y coordinate
    */
    public byte getY() {
        return handle.getIntegers().read(2).byteValue();
    }
    
    /**
     * Set bed headboard Y as block coordinate.
     * @param value - new value.
    */
    public void setY(byte value) {
        handle.getIntegers().write(2, (int) value);
    }
    
    /**
     * Retrieve bed headboard Z as block coordinate.
     * @return The current Z coordinate
    */
    public int getZ() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set bed headboard Z as block coordinate.
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(3, value);
    }
    
    /**
     * Retrieve the location of the bed.
     * @param event - the parent event.
     * @return The location.
     */
    public Location getLocation(PacketEvent event) {
    	return new Location(event.getPlayer().getWorld(), getX(), getY(), getZ());
    }
    
    /**
     * Set the location of the bed.
     * @param loc - the new location.
     */
    public void setLocation(Location loc) {
    	setX(loc.getBlockX());
    	setY((byte) loc.getBlockY());
    	setZ(loc.getBlockZ());
    }
}


