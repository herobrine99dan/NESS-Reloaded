package com.comphenix.packetwrapper;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.IntEnum;

public class WrapperPlayServerAnimation extends AbstractPacket {
    /**
     * Represents the different animations sent by notchian clients.
     * 
     * @author Kristian
     */
    public static class Animations extends IntEnum {
    	public static final int SWING_ARM = 0;
    	public static final int DAMAGE_ANIMATION = 1;
    	public static final int LEAVE_BED = 2;
    	public static final int EAT_FOOD = 3;
    	public static final int CRITICAL_EFFECT = 4;
    	public static final int MAGIC_CRITICAL_EFFECT = 5;
    	public static final int UNKNOWN = 102;
    	public static final int CROUCH = 104;
    	public static final int UNCROUCH = 105;
    	
		/**
		 * The singleton instance. Can also be retrieved from the parent class.
		 */
		private static Animations INSTANCE = new Animations();
    	
		/**
		 * Retrieve an instance of the Animation enum.
		 * @return Animation enum.
		 */
		public static Animations getInstance() {
			return INSTANCE;
		}
    }
	
	public static final PacketType TYPE = PacketType.Play.Server.ANIMATION;
    
    public WrapperPlayServerAnimation() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerAnimation(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the player ID.
     * @return The current EID
    */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the player ID.
     * @param value - new value.
    */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the player's entity object.
     * @param world - the word the player has joined.
     * @return The player's entity.
     */
    public Entity getEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the player's entity object.
     * @param event - the packet event.
     * @return The player's entity.
     */
    public Entity getEntity(PacketEvent event) {
    	return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Retrieve animation ID.
     * @see {@link WrapperPlayServerAnimation.Animations}.
     * @return The current Animation
    */
    public int getAnimation() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set animation ID.
     * @see {@link WrapperPlayServerAnimation.Animations}.
     * @param value - new value.
    */
    public void setAnimation(int value) {
        handle.getIntegers().write(1, value);
    }
}


