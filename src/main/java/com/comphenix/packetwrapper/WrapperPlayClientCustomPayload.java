package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientCustomPayload extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CUSTOM_PAYLOAD;
    
    public WrapperPlayClientCustomPayload() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientCustomPayload(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve name of the "channel" used to send the data..
     * @return The current Channel
    */
    public String getChannel() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set name of the "channel" used to send the data..
     * @param value - new value.
    */
    public void setChannel(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve length of the following byte array.
     * @return The current Length
    */
    public short getLength() {
        return handle.getIntegers().read(0).shortValue();
    }
    
    /**
     * Set length of the following byte array.
     * @param value - new value.
    */
    public void setLength(short value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve any data..
     * @return The current Data
    */
    public byte[] getData() {
        return handle.getByteArrays().read(0);
    }
    
    /**
     * Set the data to sent.
     * <p>
     * This will automatically update the length.
     * @param value - new value.
    */
    public void setData(byte[] value) {
    	setLength((short) value.length);
        handle.getByteArrays().write(0, value);
    }
}


