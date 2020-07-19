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

public class WrapperPlayServerTabComplete extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.TAB_COMPLETE;
    
    public WrapperPlayServerTabComplete() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    /**
     * Retrieve the tab-completed text alternatives.
     * @return The current Text
    */
    public String[] getText() {
        return handle.getStringArrays().read(0);
    }
    
    /**
     * Set the tab-completed text alternatives.
     * @param value - new values.
    */
    public void setText(String[] value) {
        handle.getStringArrays().write(0, value);
    }
}

