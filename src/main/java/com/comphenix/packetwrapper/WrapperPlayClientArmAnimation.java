package com.comphenix.packetwrapper;

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

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.IntEnum;

public class WrapperPlayClientArmAnimation extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.ARM_ANIMATION;
    
    /**
     * Represents the different animations sent by notchian clients.
     * 
     * @author Kristian
     */
    public static class Animations extends IntEnum {
    	public static final int NO_ANIMATION = 0;
    	public static final int SWING_ARM = 1;
    	public static final int DAMAGE_ANIMATION = 2;
    	public static final int LEAVE_BED = 3;
    	public static final int EAT_FOOD = 5;
    	public static final int UNKNOWN = 102;
    	public static final int CROUCH = 104;
    	public static final int UNCROUCH = 105;
    	
		/**
		 * The singleton instance. Can also be retrieved from the parent class.
		 */
		private static Animations INSTANCE = new Animations();
    	
		/**
		 * Retrieve an instance of the Animation enum.
		 * @return Animation enum.
		 */
		public static Animations getInstance() {
			return INSTANCE;
		}
    }
    
    public WrapperPlayClientArmAnimation() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientArmAnimation(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the player ID.
     * @return The current EID
    */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the player ID.
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
     * Retrieve animation ID.
     * @return The current Animation
    */
    public int getAnimation() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set animation ID.
     * @param value - new value.
    */
    public void setAnimation(int value) {
        handle.getIntegers().write(1, value);
    }
}