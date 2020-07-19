package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

/**
 * This is the client's response to {@link WrapperPlayServerKeepAlive}.
 */
public class WrapperPlayClientKeepAlive extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Client.KEEP_ALIVE;
    
    public WrapperPlayClientKeepAlive() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientKeepAlive(PacketContainer packet) {
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


