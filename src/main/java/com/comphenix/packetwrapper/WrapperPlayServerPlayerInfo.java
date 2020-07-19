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

public class WrapperPlayServerPlayerInfo extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.PLAYER_INFO;
    
    public WrapperPlayServerPlayerInfo() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerPlayerInfo(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the player name.
     * <p>
     * Supports chat colouring. limited to 16 characters.
     * @return The current Player name
    */
    public String getPlayerName() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set the player name.
     * <p>
     * Supports chat colouring. Limited to 16 characters.
     * @param value - new value.
    */
    public void setPlayerName(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve whether or not to remove the given player from the list of online players.
     * @return The current Online
    */
    public boolean getOnline() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set whether or not to remove the given player from the list of online players.
     * @param value - new value.
    */
    public void setOnline(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
    
    /**
     * Retrieve ping in milliseconds.
     * @return The current Ping
    */
    public short getPing() {
        return handle.getIntegers().read(0).shortValue();
    }
    
    /**
     * Set ping in milliseconds.
     * @param value - new value.
    */
    public void setPing(short value) {
        handle.getIntegers().write(0, (int) value);
    }
}


