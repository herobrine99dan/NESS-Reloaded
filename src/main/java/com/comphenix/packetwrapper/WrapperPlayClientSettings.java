package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ChatVisibility;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;

public class WrapperPlayClientSettings extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.SETTINGS;
    
    public WrapperPlayClientSettings() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientSettings(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve en_GB.
     * @return The current Locale
    */
    public String getLocale() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set en_GB.
     * @param value - new value.
    */
    public void setLocale(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve 0-3 for 'far', 'normal', 'short', 'tiny'.
     * @return The current View distance
    */
    public byte getViewDistance() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set 0-3 for 'far', 'normal', 'short', 'tiny'..
     * @param value - new value.
    */
    public void setViewDistance(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve chat settings.
     * @return The current chat settings,
    */
    public ChatVisibility getChatVisibility() {
        return handle.getChatVisibilities().read(0);
    }
    
    /**
     * Set chat settings.
     * @param value - new value.
    */
    public void setChatFlags(ChatVisibility value) {
    	handle.getChatVisibilities().write(0, value);
    }
    
    /**
     * Retrieve whether or not the colours multiplayer setting is enabled.
     * @return The current Chat colours
    */
    public boolean getChatColours() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set whether or not the colours multiplayer setting is enabled.
     * @param value - new value.
    */
    public void setChatColours(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, (boolean) value);
    }
    
    /**
     * Retrieve the client-side difficulty.
     * @return The current Difficulty
    */
    public Difficulty getDifficulty() {
        return handle.getDifficulties().read(0);
    }
    
    /**
     * Set the client-side difficulty.
     * @param value - new value.
    */
    public void setDifficulty(Difficulty difficulty) {
    	handle.getDifficulties().write(0, difficulty);
    }
    
    /**
     * Retrieve the client-side "show cape" option.
     * @return The current Show Cape
    */
    public boolean getShowCape() {
        return handle.getSpecificModifier(boolean.class).read(1);
    }
    
    /**
     * Set the client-side "show cape" option.
     * @param value - new value.
    */
    public void setShowCape(boolean value) {
        handle.getSpecificModifier(boolean.class).write(1, (boolean) value);
    }   
}