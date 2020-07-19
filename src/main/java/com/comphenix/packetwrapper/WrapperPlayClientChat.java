package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientChat extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CHAT;
    
    public WrapperPlayClientChat() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientChat(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the message sent by this player.
     * @return The current Message
    */
    public String getMessage() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set the message sent by this player.
     * @param value - new value.
    */
    public void setMessage(String value) {
        handle.getStrings().write(0, value);
    }
}


