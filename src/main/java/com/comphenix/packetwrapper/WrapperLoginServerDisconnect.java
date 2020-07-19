package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperLoginServerDisconnect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Server.DISCONNECT;
    
    public WrapperLoginServerDisconnect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginServerDisconnect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the message that is displayed to the client when the connection terminates.
     * @return The current JSON message.
    */
    public WrappedChatComponent getJsonData() {
        return handle.getChatComponents().read(0);
    }
    
    /**
     * Set the message that is displayed to the client when the connection terminates.
     * @param value - new message.
    */
    public void setJsonData(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }
}


