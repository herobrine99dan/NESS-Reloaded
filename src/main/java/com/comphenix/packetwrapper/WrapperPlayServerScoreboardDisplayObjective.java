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

public class WrapperPlayServerScoreboardDisplayObjective extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;

    /**
     * Enumeration of all the possible scoreboard positions.
     * @author Kristian
     */
    public static class Positions extends IntEnum {
    	public static final int LIST = 0;
    	public static final int SIDEBAR = 1;
    	public static final int BELOW_NAME = 2;
    	
    	private static final Positions INSTANCE = new Positions();
    	
    	public static Positions getInstance() {
    		return INSTANCE;
    	}
    }
    
    public WrapperPlayServerScoreboardDisplayObjective() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerScoreboardDisplayObjective(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the {@link Positions} of the scoreboard.
     * @return The current Position
    */
    public byte getPosition() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the {@link Positions} of the scoreboard. 
     * @param value - new value.
    */
    public void setPosition(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve the unique name for the scoreboard to be displayed..
     * @return The current Score Name
    */
    public String getScoreName() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set the unique name for the scoreboard to be displayed..
     * @param value - new value.
    */
    public void setScoreName(String value) {
        handle.getStrings().write(0, value);
    }
}