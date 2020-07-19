package com.comphenix.packetwrapper;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedAttribute;

public class WrapperPlayServerUpdateAttributes extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_ATTRIBUTES;
    
    public WrapperPlayServerUpdateAttributes() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerUpdateAttributes(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the entity's ID.
     * @return The current Entity ID
    */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the entity's ID.
     * @param value - new value.
    */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve the entity.
     * @param world - the current world of the entity.
     * @return The entity.
     */
    public Entity getEntity(World world) {
    	return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity.
     * @param event - the packet event.
     * @return The entity.
     */
    public Entity getEntity(PacketEvent event) {
    	return getEntity(event.getPlayer().getWorld());
    }
    
    /**
     * Retrieve the collection of attributes associated with the entity.
     * @return The current attributes.
    */
    public List<WrappedAttribute> getAttributes() {
        return handle.getAttributeCollectionModifier().read(0);
    }
    
    /**
     * Set the new or updated attributes associated with the entity.
     * @param value - new/updated attributes.
    */
    public void setAttributes(List<WrappedAttribute> value) {
        handle.getAttributeCollectionModifier().write(0, value);
    }
}
