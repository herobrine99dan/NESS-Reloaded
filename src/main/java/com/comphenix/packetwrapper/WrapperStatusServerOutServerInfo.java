package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public class WrapperStatusServerOutServerInfo extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Status.Server.OUT_SERVER_INFO;
    
    public WrapperStatusServerOutServerInfo() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperStatusServerOutServerInfo(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the server information to display in the multiplayer menu.
     * @return The current ping information.
    */
    public WrappedServerPing getServerPing() {
        return handle.getServerPings().read(0);
    }
    
    /**
     * Set the server information to display in the multiplayer menu.
     * @param value - new information.
    */
    public void setServerPing(WrappedServerPing value) {
        handle.getServerPings().write(0, value);
    }
}


