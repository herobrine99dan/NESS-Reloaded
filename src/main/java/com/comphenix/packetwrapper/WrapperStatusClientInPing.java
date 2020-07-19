package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperStatusClientInPing extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Status.Client.IN_PING;
    
    public WrapperStatusClientInPing() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperStatusClientInPing(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the random token we are sending.
     * @return The current random token.
    */
    public long getToken() {
        return handle.getLongs().read(0);
    }
    
    /**
     * Set the random token we are sending.
     * @param value - new token.
    */
    public void setToken(long value) {
        handle.getLongs().write(0, value);
    }   
}


