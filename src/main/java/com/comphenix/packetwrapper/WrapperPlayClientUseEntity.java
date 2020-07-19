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
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

public class WrapperPlayClientUseEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;
    
    public WrapperPlayClientUseEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientUseEntity(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the entity ID the player is interacting with.
     * @return The current Target
    */
    public int getTargetID() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Retrieve the entity the player is interacting with.
     * @param event - the world this event occured in.
     * @return The target entity.
     */
    public Entity getTarget(World world) {
    	return handle.getEntityModifier(world).read(0);
    }
    
    /**
     * Retrieve the entity the player is interacting with.
     * @param event - the current packet event.
     * @return The target entity.
     */
    public Entity getTarget(PacketEvent event) {
    	return getTarget(event.getPlayer().getWorld());
    }
    
    /**
     * Set the entity ID the player is interacting with.
     * @param value - new value.
    */
    public void setTargetID(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the use action.
     * @return The action.
    */
    public EntityUseAction getMouse() {
        return handle.getEntityUseActions().read(0);
    }
    
    /**
     * Set the use action.
     * @param value - new action.
    */
    public void setMouse(EntityUseAction value) {
        handle.getEntityUseActions().write(0, value);
    }
}


