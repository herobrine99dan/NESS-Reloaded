package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientCloseWindow extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CLOSE_WINDOW;
    
    public WrapperPlayClientCloseWindow() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientCloseWindow(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve this is the id of the window that was closed. 0 for inventory.
     * @return The current Window id
    */
    public byte getWindowId() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set this is the id of the window that was closed. 0 for inventory.
     * @param value - new value.
    */
    public void setWindowId(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
}