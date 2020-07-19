package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

/**
 * The server will frequently send out a keep-alive, each containing a random ID. 
 * <p>
 * The client must respond with the same packet. 
 */
public class WrapperPlayServerKeepAlive extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.KEEP_ALIVE;
    
    public WrapperPlayServerKeepAlive() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerKeepAlive(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the server-generated random ID.
     * @return The current keep-alive ID
    */
    public int getKeepAliveId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the server-generated random ID.
     * @param value - new ID.
    */
    public void setKeepAliveId(int value) {
        handle.getIntegers().write(0, value);
    }
}


