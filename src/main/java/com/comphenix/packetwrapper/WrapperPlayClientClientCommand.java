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
import com.comphenix.protocol.wrappers.EnumWrappers.ClientCommand;

/**
 * Sent when the client is ready to complete login and when the client is ready to respawn after death.
 * @author Kristian
 */
public class WrapperPlayClientClientCommand extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CLIENT_COMMAND;
            
    public WrapperPlayClientClientCommand() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientClientCommand(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve whether or not we're logging in or respawning.
     * @return The current command
    */
    public ClientCommand getCommand() {
        return handle.getClientCommands().read(0);
    }
    
    /**
     * Set whether or not we're logging in or respawning.
     * @param value - new value.
    */
    public void setCommand(ClientCommand value) {
        handle.getClientCommands().write(0, value);
    }
}