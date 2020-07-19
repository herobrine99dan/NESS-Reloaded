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

/**
 * Sent by the server when an iten stack is collected from the ground. This packet simply
 * initiates the collect item animation, nothing more.
 * 
 * @author Kristian
 */
public class WrapperPlayServerCollect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.COLLECT;
    
    public WrapperPlayServerCollect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerCollect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the entity ID of the item we collected.
     * @return The current collected entity ID.
    */
    public int getCollectedEntityID() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Retrieve the item stack that has been collected.
     * @param world - the current world of the item stack.
     * @return The item stack entity.
     */
    public Entity getCollectedEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the item stack that has been collected.
     * @param event - the packet event.
     * @return The item stack entity.
     */
    public Entity getCollectedEntity(PacketEvent event) {
    	return getCollectedEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Set the entity ID of the item we collected.
     * @param value - new value.
    */
    public void setCollectedEntityID(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the entity ID of the player that collected the item.
     * @return The current Collector EID
    */
    public int getCollectorEntityID() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set the entity ID of the player that collected the item.
     * @param value - new value.
    */
    public void setCollectorEntityID(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve the player that has collected the item.
     * @param world - the current world of the player.
     * @return The player.
     */
    public Entity getCollectorEntity(World world) {
    	return handle.getEntityModifier(world).read(1);
    }

    /**
     * Retrieve the player that has collected the item.
     * @param event - the packet event.
     * @return The player.
     */
    public Entity getCollectorEntity(PacketEvent event) {
    	return getCollectorEntity(event.getPlayer().getWorld());
    }
}