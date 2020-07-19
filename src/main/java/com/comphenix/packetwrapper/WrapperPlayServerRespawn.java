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

import org.bukkit.WorldType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;

public class WrapperPlayServerRespawn extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.RESPAWN;
    
    public WrapperPlayServerRespawn() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerRespawn(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve -1: The Nether, 0: The Overworld, 1: The End.
     * @return The current Dimension
    */
    public int getDimension() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set -1: The Nether, 0: The Overworld, 1: The End.
     * @param value - new value.
    */
    public void setDimension(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the difficulty level.
     * @return The current Difficulty
    */
    public Difficulty getDifficulty() {
        return handle.getDifficulties().read(0);
    }
    
    /**
     * Set the difficulty level. 
     * @param value - new value.
    */
    public void setDifficulty(Difficulty value) {
        handle.getDifficulties().write(0, value);
    }
    
    /**
     * Retrieve the game mode of the current player.
     * @return The current game mode
    */
    public NativeGameMode getGameMode() {
        return handle.getGameModes().read(0);
    }
    
    /**
     * Set the game mode of the current player.
     * @param mode - new value.
    */
    public void setGameMode(NativeGameMode mode) {
        handle.getGameModes().write(0, mode);
    }
    
    /**
     * Retrieve the current level type.
     * @return The current level type
    */
    public WorldType getLevelType() {
        return handle.getWorldTypeModifier().read(0);
    }
    
    /**
     * Set see 0x01 login.
     * @param value - new world type.
    */
    public void setLevelType(WorldType value) {
        handle.getWorldTypeModifier().write(0, value);
    }
}