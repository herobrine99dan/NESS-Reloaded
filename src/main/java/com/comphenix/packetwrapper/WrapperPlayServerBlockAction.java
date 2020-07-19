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

import org.bukkit.Instrument;
import org.bukkit.Material;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;

public class WrapperPlayServerBlockAction extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_ACTION;
    
    public WrapperPlayServerBlockAction() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerBlockAction(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    public static class BlockFaceDirection extends IntEnum {
    	public static final int DOWN = 0;
    	public static final int UP= 1;
    	public static final int SOUTH = 2;
    	public static final int WEST = 3;
    	public static final int NORTH = 4;
    	public static final int EAST = 5;
    	
		/**
		 * The singleton instance. Can also be retrieved from the parent class.
		 */
		private static BlockFaceDirection INSTANCE = new BlockFaceDirection();
    	
		/**
		 * Retrieve an instance of the direction enum.
		 * @return Direction enum.
		 */
		public static BlockFaceDirection getInstance() {
			return INSTANCE;
		}
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
    public int getY() {
        return handle.getIntegers().read(1);
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
     * Retrieve the block id this action is set for.
     * @return The current Block ID
    */
    @SuppressWarnings("deprecation")
	public short getBlockId() {
        return (short) getBlockType().getId();
    }
    
    /**
     * Set the block id this action is set for.
     * @param value - new value.
    */
    @SuppressWarnings("deprecation")
	public void setBlockId(short value) {
    	setBlockType(Material.getMaterial(value));
    }
    
    /**
     * Retrieve the block type for the block.
     * @return The current Block Type
    */
    public Material getBlockType() {
        return handle.getBlocks().read(0);
    }
    
    /**
     * Set the block type for the block.
     * @param value - new value.
    */
    public void setBlockType(Material value) {
        handle.getBlocks().write(0, value);
    }
    
    /**
     * Retrieve varies depending on block.
     * @return The current Byte 1
    */
    public byte getByte1() {
        return handle.getIntegers().read(3).byteValue();
    }
    
    /**
     * Set varies depending on block.
     * @param value - new value.
    */
    public void setByte1(byte value) {
        handle.getIntegers().write(3, (int) value);
    }
    
    /**
     * Retrieve varies depending on block.
     * @return The current Byte 2
    */
    public byte getByte2() {
        return handle.getIntegers().read(4).byteValue();
    }
    
    /**
     * Set varies depending on block.
     * @param value - new value.
    */
    public void setByte2(byte value) {
        handle.getIntegers().write(4, (int) value);
    }
    
    public NoteBlockData getNoteBlockData() {
    	return new NoteBlockData();
    }
    
    public PistionData getPistonData() {
    	return new PistionData();
    }
    
    public ChestData getChestData() {
    	return new ChestData();
    }
    
    public class NoteBlockData {
    	/**
    	 * Get the instrument type.
    	 * @return Instrument type.
    	 */
    	@SuppressWarnings("deprecation")
		public Instrument getInstrument() {
    		return Instrument.getByType(getByte1());
    	}
    	
    	/**
    	 * Set the instrument type.
    	 * @param value - new instrument type.
    	 */
    	@SuppressWarnings("deprecation")
		public void setInstrument(Instrument value) {
    		setByte1(value.getType());
    	}
    	
    	/**
    	 * Retrieve the pitch of the note block.
    	 * <p>
    	 * This is between 0-24 inclusive, where 0 is the lowest and 24 is the highest.
    	 * @return The pitch of the note block.
    	 */
    	public byte getPitch() {
    		return getByte2();
    	}
    	
    	/**
    	 * Set the pitch of the note block.
    	 * <p>
    	 * This is between 0-24 inclusive, where 0 is the lowest and 24 is the highest.
    	 * @param value - the new note block pitch.
    	 */
    	public void setPitch(byte value) {
    		setByte2(value);
    	}
    }
    
    public class PistionData {
    	/**
    	 * Get the state the piston changes to.
    	 * <p>
    	 * Zero is pushing, one is pulling.  
    	 * @return Piston state.
    	 */
    	public byte getState() {
    		return getByte1();
    	}
    	
    	/**
    	 * Set the state the piston changes to.
    	 * <p>
    	 * Zero is pushing, one is pulling.  
    	 * @return Piston state.
    	 */
    	public void setState(byte value) {
    		setByte1(value);
    	}
    	
    	/**
    	 * Get the block face direction the piston is pushing.
    	 * <p>
    	 * See {@link BlockFaceDirection} for more information.
    	 * @return The direction.
    	 */
    	public int getDirection() {
    		return getByte2();
    	}
    	
    	/**
    	 * Set the block face  direction the piston is pushing.
    	 * <p>
    	 * See {@link BlockFaceDirection} for more information.
    	 * @return The direction.
    	 */
    	public void setDirection(int value) {
    		setByte2((byte) value);
    	}
    }
    
    public class ChestData {
    	public boolean isOpen() {
    		return getByte2() != 0;
    	}
    	
    	public void setOpen(boolean open) {
    		setByte2(open ? (byte)1 : (byte)0);
    	}
    }
}


