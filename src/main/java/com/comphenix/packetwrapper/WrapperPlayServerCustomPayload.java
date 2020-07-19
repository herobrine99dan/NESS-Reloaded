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

public class WrapperPlayServerCustomPayload extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CUSTOM_PAYLOAD;
    
    public WrapperPlayServerCustomPayload() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerCustomPayload(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve name of the "channel" used to send the data..
     * @return The current Channel
    */
    public String getChannel() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set name of the "channel" used to send the data..
     * @param value - new value.
    */
    public void setChannel(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve the custom data that sent in this message.
     * @return The current data
    */
    public byte[] getData() {
        return handle.getByteArrays().read(0);
    }
    
    /**
     * Set the custom data that is sent.
     * @param value - new value.
    */
    public void setData(byte[] value) {
        handle.getByteArrays().write(0, value);
    }
}


