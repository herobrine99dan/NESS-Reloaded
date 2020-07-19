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

public class WrapperPlayServerScoreboardObjective extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_OBJECTIVE;
        
    /**
     * Enumeration of all the known packet modes.
     * 
     * @author Kristian
     */
    public static class Modes extends IntEnum {
    	public static final int ADD_OBJECTIVE = 0;
    	public static final int REMOVE_OBJECTIVE = 1;
    	public static final int UPDATE_VALUE = 2;

    	private static final Modes INSTANCE = new Modes();
    	
    	public static Modes getInstance() {
    		return INSTANCE;
    	}
    }
    
    public WrapperPlayServerScoreboardObjective() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerScoreboardObjective(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve an unique name for the objective.
     * @return The current Objective name
    */
    public String getObjectiveName() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set an unique name for the objective.
     * @param value - new value.
    */
    public void setObjectiveName(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve the text to be displayed for the score.
     * @return The current Objective value
    */
    public String getObjectiveValue() {
        return handle.getStrings().read(1);
    }
    
    /**
     * Set the text to be displayed for the score.
     * @param value - new value.
    */
    public void setObjectiveValue(String value) {
        handle.getStrings().write(1, value);
    }
    
    /**
     * Retrieve the current packet {@link Modes}. 
     * <p>
     * This determines if the objective is added or removed.
     * @see {@link WrapperPlayServerScoreboardObjective.Modes}
     * @return The current Create/Remove
    */
    public byte getPacketMode() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the current packet {@link Modes}. 
     * <p>
     * This determines if the objective is added or removed.
     * @see {@link WrapperPlayServerScoreboardObjective.Modes}
     * @param value - new value.
    */
    public void setPacketMode(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
}