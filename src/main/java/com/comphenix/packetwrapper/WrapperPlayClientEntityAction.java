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
import com.comphenix.protocol.reflect.IntEnum;

public class WrapperPlayClientEntityAction extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.ENTITY_ACTION;

    /**
     * Enumeration of all the entity actions.
     * @author Kristian
     */
    public static class Action extends IntEnum {
    	public static final int CROUCH = 1;
    	public static final int UNCROUCH = 2;
    	public static final int LEAVE_BED = 3;
    	public static final int START_SPRINTING = 4;
    	public static final int STOP_SPRINTING = 5;
    	
    	private static final Action INSTANCE = new Action();
    	
    	public static Action getInstance() {
    		return INSTANCE;
    	}
    }
    
    public WrapperPlayClientEntityAction() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientEntityAction(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve player ID.
     * @return The current EID
    */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set player ID.
     * @param value - new value.
    */
    public void setEntityID(int value) {
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
     * Retrieve the ID of the action.
     * @see {@link WrapperPlayClientEntityAction.Action}
     * @return The current Action ID
    */
    public byte getActionId() {
        return handle.getIntegers().read(1).byteValue();
    }
    
    /**
     * Set the ID of the action, see below.
     * @see {@link WrapperPlayClientEntityAction.Action}
     * @param value - new value.
    */
    public void setActionId(byte value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve horse jump boost. Ranged from 0 -> 100..
     * @return The current Jump Boost
    */
    public int getJumpBoost() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set horse jump boost. Ranged from 0 -> 100..
     * @param value - new value.
    */
    public void setJumpBoost(int value) {
        handle.getIntegers().write(2, value);
    }
}