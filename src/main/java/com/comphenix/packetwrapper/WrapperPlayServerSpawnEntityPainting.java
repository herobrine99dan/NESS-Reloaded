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
import org.bukkit.entity.Painting;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;

public class WrapperPlayServerSpawnEntityPainting extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_PAINTING;
   
    private static PacketConstructor entityConstructor;
    
    public WrapperPlayServerSpawnEntityPainting() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerSpawnEntityPainting(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrapperPlayServerSpawnEntityPainting(Painting painting) {
        super(fromPainting(painting), TYPE);
    }
    
    // Useful constructor
    private static PacketContainer fromPainting(Painting painting) {
        if (entityConstructor == null)
        	entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, (Entity) painting);
        return entityConstructor.createPacket((Entity) painting);
    }
    
    /**
     * Retrieve unique entity ID.
     * @return The current Entity ID
    */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Retrieve the entity of the painting that will be spawned.
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    public Entity getEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     * @param event - the packet event.
     * @return The spawned entity.
     */
    public Entity getEntity(PacketEvent event) {
    	return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Set unique entity ID.
     * @param value - new value.
    */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve name of the painting; max length 13 (length of "SkullAndRoses").
     * @return The current Title
    */
    public String getTitle() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set name of the painting; max length 13 (length of "SkullAndRoses").
     * @param value - new value.
    */
    public void setTitle(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve center X coordinate.
     * @return The current X
    */
    public int getX() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set center X coordinate.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve center Y coordinate.
     * @return The current Y
    */
    public int getY() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set center Y coordinate.
     * @param value - new value.
    */
    public void setY(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve center Z coordinate.
     * @return The current Z
    */
    public int getZ() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set center Z coordinate.
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(3, value);
    }
    
    /**
     * Retrieve direction the painting faces.
     * <p>
     * Here zero is -z, one is -x, two is +z and three is +x.
     * @return The current Direction
    */
    public int getDirection() {
        return handle.getIntegers().read(4);
    }
    
    /**
     * Set direction the painting faces.
     * <p>
     * Here zero is -z, one is -x, two is +z and three is +x.
     * @param value - new value.
    */
    public void setDirection(int value) {
        handle.getIntegers().write(4, value);
    }
}