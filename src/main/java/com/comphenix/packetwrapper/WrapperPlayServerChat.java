package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerChat extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CHAT;
    
    public WrapperPlayServerChat() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerChat(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the chat message.
     * @return The current JSON Data
    */
    public WrappedChatComponent getMessage() {
        return handle.getChatComponents().read(0);
    }
    
    /**
     * Set the chat message.
     * @param value - new value.
    */
    public void setMessage(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }
}


