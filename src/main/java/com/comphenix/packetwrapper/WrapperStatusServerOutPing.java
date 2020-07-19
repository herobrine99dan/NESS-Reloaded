package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperStatusServerOutPing extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Status.Server.OUT_PING;
    
    public WrapperStatusServerOutPing() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperStatusServerOutPing(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the random token that should be the same as sent by the client.
     * @return The current token.
    */
    public long getTime() {
        return handle.getLongs().read(0);
    }
    
    /**
     * Set the random token that should be the same as sent by the client.
     * @param value - new token.
    */
    public void setToken(long value) {
        handle.getLongs().write(0, value);
    }
}


