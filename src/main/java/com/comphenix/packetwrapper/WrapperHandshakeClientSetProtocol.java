package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Protocol;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperHandshakeClientSetProtocol extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Handshake.Client.SET_PROTOCOL;
    
    public WrapperHandshakeClientSetProtocol() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperHandshakeClientSetProtocol(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the new protocol version. 
     * <p>
     * This is 4 as of 1.7.2. Note to be confused with the old protocol versions.
     * @return The current Protocol Version
    */
    public int getProtocolVersion() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the new protocol version.
     * <p>
     * This is 4 as of 1.7.2.
     * @param value - new value.
    */
    public void setProtocolVersion(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the server hostname or IP.
     * @return The current server hostname.
    */
    public String getServerHostname() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set the server hostname.
     * @param value - new value.
    */
    public void setServerHostname(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve the TCP port number, typically 25565.
     * @return The current server port
    */
    public short getServerPort() {
        return handle.getIntegers().read(1).shortValue();
    }
    
    /**
     * Set the TCP port number.
     * @param value - new value.
    */
    public void setServerPort(short value) {
        handle.getIntegers().write(1, (int) value);
    }
    
    /**
     * Retrieve the next protocol to use.
     * @return The next protocol.
    */
    public Protocol getNextProtocol() {
        return handle.getProtocols().read(0);
    }
    
    /**
     * Set the next protocol to use.
     * @param value - new protocll.
    */
    public void setNextProtocol(Protocol value) {
        handle.getProtocols().write(0, value);
    }
}


