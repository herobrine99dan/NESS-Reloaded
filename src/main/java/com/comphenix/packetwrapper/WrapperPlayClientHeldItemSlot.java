package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientHeldItemSlot extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.HELD_ITEM_SLOT;
    
    public WrapperPlayClientHeldItemSlot() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientHeldItemSlot(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the slot which the player has selected (0-8).
     * @return The current Slot
    */
    public short getSlot() {
        return handle.getIntegers().read(0).shortValue();
    }
    
    /**
     * Set the slot which the player has selected (0-8).
     * @param value - new value.
    */
    public void setSlot(short value) {
        handle.getIntegers().write(0, (int) value);
    }
}