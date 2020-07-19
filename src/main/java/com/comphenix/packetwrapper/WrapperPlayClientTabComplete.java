package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

/**
 * Sent when the user presses [tab] while writing text.
 * @author Kristian
 */
public class WrapperPlayClientTabComplete extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.TAB_COMPLETE;
    
    public WrapperPlayClientTabComplete() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientTabComplete(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve all the text currently behind the cursor. 
     * @return The current Text
    */
    public String getText() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set all the text currently behind the cursor. 
     * @param value - new value.
    */
    public void setText(String value) {
        handle.getStrings().write(0, value);
    }   
}


