/*
 *  PacketWrapper - Contains wrappers for each packet in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2 of 
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; 
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 *  02111-1307 USA
 */

package com.comphenix.packetwrapper;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Preconditions;

public class WrapperPlayServerNamedEntitySpawn extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.NAMED_ENTITY_SPAWN;
    
    // For constructing packets from entities
	private static PacketConstructor entityConstructor;
    
    public WrapperPlayServerNamedEntitySpawn() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerNamedEntitySpawn(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    public WrapperPlayServerNamedEntitySpawn(Player player) {
        super(fromPlayer(player), TYPE);
    }
    
    // Useful constructor
    private static PacketContainer fromPlayer(Player player) {
        if (entityConstructor == null)
        	entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, player);
        return entityConstructor.createPacket(player);
    }
    
    /**
     * Retrieve player ID.
     * @return The current EID
    */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set player ID.
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
     * Retrieve the player name.
     * <p>
     * Max length of 16.
     * @return The current player Name, or NULL if not set.
    */
    public String getPlayerName() {
    	WrappedGameProfile profile = getProfile();
        return profile != null ? profile.getName() : null;
    }
    
    /**
     * Set the player name.
     * <p>
     * Max length of 16.
     * @param value - new value.
    */
    public void setPlayerName(String value) {
    	if (value != null && value.length() > 16)
    		throw new IllegalArgumentException("Maximum player name lenght is 16 characters.");
    	setProfile(new WrappedGameProfile(getPlayerUUID(), value));
    }
    
    /**
     * Retrieve the UUID of the player.
     * @return The UUID, or NULL if not set.
     */
    public String getPlayerUUID() {
    	WrappedGameProfile profile = getProfile();
        return profile != null ? profile.getId() : null;
    }
    
    /**
     * Set the UUID of the player.
     * @param uuid - the UUID.
     */
    public void setPlayerUUID(String uuid) {
    	setProfile(new WrappedGameProfile(uuid, getPlayerName()));
    }

    /**
     * Retrieve player's full profile.
     * @return The spawner player's profile.
    */
    public WrappedGameProfile getProfile() {
        return handle.getGameProfiles().read(0);
    }
    
    /**
     * Set the spawned player's profile.
     * @param value - new profile.
    */
    public void setProfile(WrappedGameProfile value) {
        handle.getGameProfiles().write(0, value);
    }
    
    /**
     * Retrieve the position of the spawned entity as a vector.
     * @return The position as a vector.
     */
    public Vector getPosition() {
    	return new Vector(getX(), getY(), getZ());
    }
    
    /**
     * Set the position of the spawned entity using a vector.
     * @param position - the new position.
     */
    public void setPosition(Vector position) {
    	setX(position.getX());
    	setY(position.getY());
    	setZ(position.getZ());
    }
    
    /**
     * Retrieve the x axis of the position.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     * @return The current X
    */
    public double getX() {
        return handle.getIntegers().read(1) / 32.0D;
    }
    
    /**
     * Set the x axis of the position.
     * @param value - new value.
    */
    public void setX(double value) {
        handle.getIntegers().write(1, (int) Math.floor(value * 32.0D));
    }
    
    /**
     * Retrieve the y axis of the position.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     * @return The current y
    */
    public double getY() {
        return handle.getIntegers().read(2) / 32.0D;
    }
    
    /**
     * Set the y axis of the position.
     * @param value - new value.
    */
    public void setY(double value) {
        handle.getIntegers().write(2, (int) Math.floor(value * 32.0D));
    }
    
    /**
     * Retrieve the z axis of the new position.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     * @return The current z
    */
    public double getZ() {
        return handle.getIntegers().read(3) / 32.0D;
    }
    
    /**
     * Set the z axis of the new position.
     * @param value - new value.
    */
    public void setZ(double value) {
        handle.getIntegers().write(3, (int) Math.floor(value * 32.0D));
    }
    
    /**
     * Retrieve the yaw of the spawned entity.
     * @return The current Yaw
    */
    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }
    
    /**
     * Set the yaw of the spawned entity.
     * @param value - new yaw.
    */
    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }
    
    /**
     * Retrieve the pitch of the spawned entity.
     * @return The current pitch
    */
    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }
    
    /**
     * Set the pitch of the spawned entity.
     * @param value - new pitch.
    */
    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }
    
    /**
     * Retrieve the item the player is currently holding. 
     * <p>
     * Note that this should be 0 for "no item", unlike -1 used in other packets. A negative value crashes clients.
     * @return The current item.
    */
    public short getCurrentItem() {
        return handle.getIntegers().read(4).shortValue();
    }
    
    /**
     * Set the item the player is currently holding. 
     * <p>
     * Note that this should be 0 for "no item", unlike -1 used in other packets. A negative value crashes clients.
     * @param value - new value.
    */
    public void setCurrentItem(short value) {
        handle.getIntegers().write(4, (int) value);
    }
    
    /**
     * Retrieve the associated metadata object.
     * <p>
     * Note that the 1.3 client crashes on packets with no metadata, but the server can send any metadata 
     * key of 0, 1 or 8 and the client is fine.
     * @return The current metadata.
    */
    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }
    
    /**
     * Set the associated metadata object. 
     * <p>
     * Note that the 1.3 client crashes on packets with no metadata, but the server can send any metadata 
     * key of 0, 1 or 8 and the client is fine..
     * @param value - new value.
    */
    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
    
    @Override
    public PacketContainer getHandle() {
    	Preconditions.checkNotNull(getPlayerName(), "Must specify a player name.");
    	Preconditions.checkNotNull(getPlayerUUID(), "Must specify a player UUID.");
    	return super.getHandle();
    }
}