package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerKickDisconnect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.KICK_DISCONNECT;
    
    public WrapperPlayServerKickDisconnect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerKickDisconnect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the reason that is displayed to the client when the connection terminates. 
     * @return The current Reason
    */
    public WrappedChatComponent getReason() {
        return handle.getChatComponents().read(0);
    }
    
    /**
     * Set the reason that is displayed to the client when the connection terminates.
     * @param value - new reason.
    */
    public void setReason(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }   
}