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

import org.bukkit.GameMode;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerGameStateChange extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.GAME_STATE_CHANGE;
    
    public WrapperPlayServerGameStateChange() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerGameStateChange(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Enumeration of all the reason codes in Minecraft.
     * 
     * @author Kristian
     */
    public static class Reasons {
    	public static final int INVALID_BED = 0;
    	public static final int BEGIN_RAINING = 1;
    	public static final int END_RAINING = 2;
    	public static final int CHANGE_GAME_MODE = 3;
    	public static final int ENTER_CREDITS = 4;
    	
    	/**
    	 * Show demo screen. 101 - Tell movement controls, 102 - Tell jump control, 103 - Tell inventory control.
    	 */
    	public static final int DEMO_MESSAGES = 5;
    	
    	/**
    	 * Value: Appears to be played when an arrow strikes another player in Multiplayer 
    	 */
    	public static final int ARROW_HITTING_PLAYER = 6;
    	
    	/**
    	 * Value: The current darkness value. 1 = Dark, 0 = Bright.
    	 */
    	public static final int SKY_FADE_VALUE = 7;
    	
    	/**
    	 * Value: Time in ticks for the sky to fade 
    	 */
    	public static final int SKY_FADE_TIME = 8;
    	
    	private static final Reasons INSTANCE = new Reasons();
    	
    	/**
    	 * Retrieve the reasons enum.
    	 * @return Reasons enum.
    	 */
    	public static Reasons getInstance() {
    		return INSTANCE;
    	}
    }
    
    /**
     * Retrieve the reason the game state changed.
     * @see {@link Reasons}.
     * @return The current Reason
    */
    public int getReason() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the reason the game state changed.
     * @see {@link Reasons}.
     * @param value - new value.
    */
    public void setReason(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the new game mode.
     * <p>
     * Only used when reason is 3.
     * @return The current Game mode
    */
    @SuppressWarnings("deprecation")
	public GameMode getGameMode() {
        return GameMode.getByValue(handle.getIntegers().read(1));
    }
    
    /**
     * Set the new game mode.
     * <p>
     * Only used when reason is 3.
     * @param value - new value.
    */
    @SuppressWarnings("deprecation")
	public void setGameMode(GameMode value) {
        handle.getIntegers().write(1, value.getValue());
    }
}

