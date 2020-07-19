package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class WrapperLoginClientStart extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Client.START;
    
    public WrapperLoginClientStart() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginClientStart(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the initial game profile.
     * <p>
     * Note that the UUID is NULL.
     * @return The current profile.
    */
    public WrappedGameProfile getProfile() {
        return handle.getGameProfiles().read(0);
    }
    
    /**
     * Set the initial game profile.
     * @param value - new profile.
    */
    public void setProfile(WrappedGameProfile value) {
        handle.getGameProfiles().write(0, value);
    }   
}

