package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class WrapperLoginServerSuccess extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Server.SUCCESS;
    
    public WrapperLoginServerSuccess() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginServerSuccess(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the UUID and player name of the connected client.
     * @return The current client profile.
    */
    public WrappedGameProfile getProfile() {
        return handle.getGameProfiles().read(0);
    }
    
    /**
     * Set the UUID and player name of the connected client as a game profile.
     * @param value - new profile.
    */
    public void setProfile(WrappedGameProfile value) {
        handle.getGameProfiles().write(0, value);
    }
}


