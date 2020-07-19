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
import com.comphenix.protocol.reflect.IntEnum;

public class WrapperPlayClientBlockDig extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.BLOCK_DIG;
        
    public enum Status {
    	STARTED_DIGGING,    // 0
    	CANCELLED_DIGGING,  // 1
    	FINISHED_DIGGING,   // 2
    	DROP_ITEM_STACK,    // 3
    	DROP_ITEM, 	        // 4
    	
    	/**
    	 * Shooting arrows or finishing eating.
    	 */
    	SHOOT_ARROW 	    // 5
    }
    
    public static class BlockSide extends IntEnum {
    	/**
    	 * Represents the bottom face of a block. 
    	 * <p>
    	 * Offset: -Y
    	 */
    	public static final int BOTTOM = 0;
    	
    	/**
    	 * Represents the top face of a block. 
    	 * <p>
    	 * Offset: Y
    	 */
    	public static final int TOP = 1;
    	
    	/**
    	 * Represents the face behind the block as seen by the player. 
    	 * <p>
    	 * Offset: -Z
    	 */
    	public static final int BEHIND = 2;
    	
    	/**
    	 * Represents the face in front of the block as seen by the player. 
    	 * <p>
    	 * Offset: Z
    	 */
    	public static final int FRONT = 3;
    	
    	/**
    	 * Represents the face to the left of the block as seen by the player. 
    	 * <p>
    	 * Offset: -X
    	 */
    	public static final int LEFT = 4;
    	
    	/**
    	 * Represents the face to the right of the block as seen by the player. 
    	 * <p>
    	 * Offset: X
    	 */
    	public static final int RIGHT = 5;
    	
		/**
		 * The singleton instance. Can also be retrieved from the parent class.
		 */
		private static BlockSide INSTANCE = new BlockSide();
    	
		/**
		 * Retrieve an instance of the BlockSide enum.
		 * @return BlockSide enum.
		 */
		public static BlockSide getInstance() {
			return INSTANCE;
		}
		
		private static final int[] xOffset = new int[] { 0, 0, 0, 0, -1, 1 };
		private static final int[] yOffset = new int[] { -1, 1, 0, 0, 0, 0 };
		private static final int[] zOffset = new int[] { 0, 0, -1, 1, 0, 0 };
		
		public static int getXOffset(int blockFace) {
			return xOffset[blockFace];
		}
		
		public static int getYOffset(int blockFace) {
			return yOffset[blockFace];
		}
		
		public static int getZOffset(int blockFace) {
			return zOffset[blockFace];
		}
		
		// We only allow a single instance of this class
	    private BlockSide() {
			super();
		}
    }
    
    public WrapperPlayClientBlockDig() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientBlockDig(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the action the player is taking against the block.
     * @return The current Status
    */
    public Status getStatus() {
        return Status.values()[handle.getIntegers().read(4)];
    }
    
    /**
     * Set the action the player is taking against the block.
     * @param value - new action.
    */
    public void setStatus(Status value) {
        handle.getIntegers().write(4, value.ordinal());
    }
    
    /**
     * Retrieve block position.
     * @return The current X
    */
    public int getX() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set block position.
     * @param value - new value.
    */
    public void setX(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve block position.
     * @return The current Y
    */
    public byte getY() {
        return handle.getIntegers().read(1).byteValue();
    }
    
    /**
     * Set block position.
     * @param value - new value.
    */
    public void setY(byte value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve block position.
     * @return The current Z
    */
    public int getZ() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set block position.
     * @param value - new value.
    */
    public void setZ(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve the face being hit. See {@link Packet0EPlayerDigging.BlockSide BlockSide}.
     * @return The current block side.
    */
    public int getFace() {
        return handle.getIntegers().read(3).byteValue();
    }
    
    /**
     * Set the face being hit. See {@link Packet0EPlayerDigging.BlockSide BlockSide}.
     * @param value - new value.
    */
    public void setFace(int value) {
        handle.getIntegers().write(3, value);
    }
}