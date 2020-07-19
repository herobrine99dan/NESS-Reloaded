package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperLoginClientEncryptionBegin extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Client.ENCRYPTION_BEGIN;
    
    public WrapperLoginClientEncryptionBegin() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginClientEncryptionBegin(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the shared secret data.
     * @return The shared data.
    */
    public byte[] getSharedSecret() {
        return handle.getByteArrays().read(0);
    }
    
    /**
     * Set the shared secret data.
     * @param value - new value.
    */
    public void setSharedSecret(byte[] value) {
        handle.getByteArrays().write(0, value);
    }
    
    /**
     * Retrieve the token response.
     * @return The shared data.
    */
    public byte[] getVerifyTokenResponse() {
        return handle.getByteArrays().read(1);
    }
    
    /**
     * Set token reponse.
     * @param value - new value.
    */
    public void setVerifyTokenResponse(byte[] value) {
        handle.getByteArrays().write(1, value);
    }
}


