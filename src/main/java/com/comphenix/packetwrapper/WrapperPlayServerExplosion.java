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
import java.util.List;

import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkPosition;

public class WrapperPlayServerExplosion extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.EXPLOSION;
    
    public WrapperPlayServerExplosion() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerExplosion(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the x coordinate of the center of the explosion.
     * @return The current X
    */
    public double getX() {
        return handle.getDoubles().read(0);
    }
    
    /**
     * Set the x coordinate of the center of the explosion.
     * @param value - new value.
    */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }
    
    /**
     * Retrieve the y coordinate of the center of the explosion.
     * @return The current Y
    */
    public double getY() {
        return handle.getDoubles().read(1);
    }
    
    /**
     * Set the y coordinate of the center of the explosion.
     * @param value - new value.
    */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }
    
    /**
     * Retrieve the z coordinate of the center of the explosion.
     * @return The current Z
    */
    public double getZ() {
        return handle.getDoubles().read(2);
    }
    
    /**
     * Set the z coordinate of the center of the explosion.
     * @param value - new value.
    */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }
    
    /**
     * Retrieve the explosion radius.
     * <p>
     * Note: Currently unused in the client.
     * @return The current Radius
    */
    public float getRadius() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set the explosion radius.
     * <p>
     * Note: Currently unused in the client.
     * @param value - new value.
    */
    public void setRadius(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve the absolute coordinates of each affected block in the explosion.
     * @return The current records.
    */
    public List<ChunkPosition> getRecords() {
        return handle.getPositionCollectionModifier().read(0);
    }
    
    /**
     * Set the absolute coordinates of each affected block in the explosion.
     * @param value - new value.
    */
    public void setRecords(List<ChunkPosition> value) {
        handle.getPositionCollectionModifier().write(0, value);
    }
    
    /**
     * Retrieve x velocity of the player being pushed by the explosion.
     * @return The current Player Motion X
    */
    public float getPlayerMotionX() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set x velocity of the player being pushed by the explosion.
     * @param value - new value.
    */
    public void setPlayerMotionX(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve y velocity of the player being pushed by the explosion.
     * @return The current Player Motion Y
    */
    public float getPlayerMotionY() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set y velocity of the player being pushed by the explosion.
     * @param value - new value.
    */
    public void setPlayerMotionY(float value) {
        handle.getFloat().write(1, value);
    }
    
    /**
     * Retrieve z velocity of the player being pushed by the explosion.
     * @return The current Player Motion Z
    */
    public float getPlayerMotionZ() {
        return handle.getFloat().read(2);
    }
    
    /**
     * Set z velocity of the player being pushed by the explosion.
     * @param value - new value.
    */
    public void setPlayerMotionZ(float value) {
        handle.getFloat().write(2, value);
    }
    
    /**
     * Retrieve velocity of the player being pushed by the explosion.
     * @return New velocity.
     */
    public Vector getPlayerMotion() {
    	return new Vector(getPlayerMotionX(), getPlayerMotionY(), getPlayerMotionZ());
    }
    
    /**
     * Set the velocity of the player being pushed by the explosion.
     * @param motion - new velocity.
     */
    public void setPlayerMotion(Vector motion) {
    	setPlayerMotionX((float) motion.getX());
    	setPlayerMotionY((float) motion.getY());
    	setPlayerMotionZ((float) motion.getZ());
    }
}
