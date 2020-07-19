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

import javax.annotation.Nonnull;

import org.bukkit.Location;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class WrapperPlayClientUpdateSign extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.UPDATE_SIGN;
    
    public WrapperPlayClientUpdateSign() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientUpdateSign(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve block X Coordinate.
     * @return The current X
    */
    public int getX() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set block X Coordinate.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve block Y Coordinate.
     * @return The current Y
    */
    public short getY() {
        return handle.getIntegers().read(1).shortValue();
    }
    
    /**
     * Set block Y Coordinate.
     * @param value - new value.
    */
    public void setY(short value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve block Z Coordinate.
     * @return The current Z
    */
    public int getZ() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set block Z Coordinate.
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve the location of the current particle.
     * @param event - the packet event.
     * @return The location.
     */
    public Location getLocation(PacketEvent event) {
    	return new Location(event.getPlayer().getWorld(), getX(), getY(), getZ());
    }

    /**
     * Set the location of the particle to send.
     * @param loc - the location.
     */
    public void setLocation(Location loc) {
    	if (loc == null)
    		throw new IllegalArgumentException("Location cannot be NULL.");
    	setX(loc.getBlockX());
    	setY((short) loc.getBlockY());
    	setZ(loc.getBlockZ());
    }
    
    /**
     * Retrieve the lines of text represented by a four-element String array.
     * @return The current lines.
    */
    public String[] getLines() {
        return handle.getStringArrays().read(0);
    }
    
    /**
     * Set the lines of text represented by a four-element String array..
     * @param value - new value.
    */
    public void setLines(@Nonnull String[] lines) {
    	if (lines == null)
    		throw new IllegalArgumentException("Array cannot be NULL.");
    	if (lines.length != 4)
    		throw new IllegalArgumentException("The lines array must be four elements long.");
        handle.getStringArrays().write(0, lines);
    }
}