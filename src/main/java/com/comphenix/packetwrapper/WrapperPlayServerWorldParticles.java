package com.comphenix.packetwrapper;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class WrapperPlayServerWorldParticles extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_PARTICLES;
        
    /**
     * Every particle recognized by the client.
     * @author Kristian
     */
    public enum ParticleEffect {
		HUGE_EXPLOSION("hugeexplosion"),
		LARGE_EXPLODE("largeexplode"),
		FIREWORKS_SPARK("fireworksSpark"),
		BUBBLE("bubble"),
		SUSPEND("suspend"),
		DEPTH_SUSPEND("depthSuspend"),
		TOWN_AURA("townaura"),
		CRIT("crit"),
		MAGIC_CRIT("magicCrit"),
		MOB_SPELL("mobSpell"),
		MOB_SPELL_AMBIENT("mobSpellAmbient"),
		SPELL("spell"),
		INSTANT_SPELL("instantSpell"),
		WITCH_MAGIC("witchMagic"),
		NOTE("note"),
		PORTAL("portal"),
		ENCHANTMENT_TABLE("enchantmenttable"),
		EXPLODE("explode"),
		FLAME("flame"),
		LAVA("lava"),
		FOOTSTEP("footstep"),
		SPLASH("splash"),
		LARGE_SMOKE("largesmoke"),
		CLOUD("cloud"),
		RED_DUST("reddust"),
		SNOWBALL_POOF("snowballpoof"),
		DRIP_WATER("dripWater"),
		DRIP_LAVA("dripLava"),
		SNOW_SHOVEL("snowshovel"),
		SLIME("slime"),
		HEART("heart"),
		ANGRY_VILLAGER("angryVillager"),
		HAPPY_VILLAGER("happyVillager"),
		ICONCRACK("iconcrack_"),
		TILECRACK("tilecrack_");
       
	    private final String name;
	    
	    // Fast lookup of effects
	    private volatile static Map<String, ParticleEffect> LOOKUP = generateLookup();
	   
	    /**
	     * Generate a fast string lookup of every particle effect.
	     * @return A string lookup.
	     */
	    private static Map<String, ParticleEffect> generateLookup() {
    		Map<String, ParticleEffect> created = new HashMap<String, ParticleEffect>();
    		
    		// Update the thread local copy first - avoid potential concurrency issues
    		for (ParticleEffect effect : values())
    			created.put(effect.getParticleName(), effect);
    		return created;
	    }
	    
	    private ParticleEffect(String name) {
	        this.name = name;
	    }
	    
	    /**
	     * Retrieve the particle effect from a corresponding name. 
	     * @param name - the particle name.
	     * @return The effect, or NULL if not found.
	     */
	    public static ParticleEffect fromName(String name) {
	    	return LOOKUP.get(name);
	    }
	    
	    /**
	     * Retrieve the particle name.
	     * @return The particle name.
	     */
	    public String getParticleName() {
			return name;
		}
    }
    
    /**
     * Construct a new particle packet.
     */
    public WrapperPlayServerWorldParticles() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    /**
     * Construct a particle packet that reads and modifies a given native packet.
     * @param packet - the native packet.
     */
    public WrapperPlayServerWorldParticles(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Constructs a new particle packet with the given parameters.
     * @param effect - the particle effect.
     * @param count - the number of particles to spawn.
     * @param location - the spawn location.
     * @param offset - the random offset that will be applied to each particle.
     */
    public WrapperPlayServerWorldParticles(ParticleEffect effect, int count, Location location, Vector offset) {
    	this();
    	setParticleEffect(effect);
    	setNumberOfParticles(count);
    	setLocation(location);
    	setOffset(offset);
    }
    
    /**
     * Retrieve the name of the particle to create. A list can be found here.
     * @return The current Particle name
    */
    public String getParticleName() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set the name of the particle to create. A list can be found here.
     * @param value - new value.
    */
    public void setParticleName(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve the particle effect.
     * @return The particle effect, or NULL if not known.
     */
    public ParticleEffect getParticleEffect() {
    	return ParticleEffect.fromName(getParticleName());
    }
    
    /**
     * Set the particle effect to use.
     * @param effect - the particle effect.
     */
    public void setParticleEffect(ParticleEffect effect) {
    	if (effect == null)
    		throw new IllegalArgumentException("effect cannot be NULL.");
    	setParticleName(effect.getParticleName());
    }
    
    /**
     * Retrieve the location of the current particle.
     * @param event - the packet event.
     * @return The location.
     */
    public Location getLocation(PacketEvent event) {
    	return getLocation(event.getPlayer().getWorld());
    }
    
    /**
     * Retrieve the location of the current particle.
     * @param world - the containing world.
     * @return The location.
     */
    public Location getLocation(World world) {
    	return new Location(world, getX(), getY(), getZ());
    }
    
    /**
     * Set the location of the particle to send.
     * @param loc - the location.
     */
    public void setLocation(Location loc) {
    	if (loc == null)
    		throw new IllegalArgumentException("Location cannot be NULL.");
    	setX((float) loc.getX());
    	setY((float) loc.getY());
    	setZ((float) loc.getZ());
    }
    
    /**
     * Set the random offset (multiplied by a random gaussian) to be applied after the particles are created.
     * @param vector - the random vector offset.
     */
    public void setOffset(Vector vector) {
    	if (vector == null)
    		throw new IllegalArgumentException("Vector cannot be NULL.");
    	setOffsetX((float) vector.getX());
    	setOffsetY((float) vector.getY());
    	setOffsetZ((float) vector.getZ());
    }
    
    /**
     * Retrieve the random offset that will be multiplied by a random gaussian and applied to each created particle.
     * @return The random offset.
     */
    public Vector getOffset() {
    	return new Vector(getX(), getY(), getZ());
    }
    
    /**
     * Retrieve the x position of the particle.
     * @return The current position.
    */
    public float getX() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set the x position of the particle.
     * @param value - new position.
    */
    public void setX(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve the y position of the particle.
     * @return The current Y position.
    */
    public float getY() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set the y position of the particle.
     * @param value - new position.
    */
    public void setY(float value) {
        handle.getFloat().write(1, value);
    }
    
    /**
     * Retrieve the z position of the particle.
     * @return The current Z position.
    */
    public float getZ() {
        return handle.getFloat().read(2);
    }
    
    /**
     * Set the z position of the particle.
     * @param value - new position.
    */
    public void setZ(float value) {
        handle.getFloat().write(2, value);
    }
    
    /**
     * Retrieve the offset added to the X position after being multiplied by random.nextGaussian().
     * @return The current Offset X
    */
    public float getOffsetX() {
        return handle.getFloat().read(3);
    }
    
    /**
     * Set this the offset added to the X position after being multiplied by random.nextGaussian().
     * @param value - new value.
    */
    public void setOffsetX(float value) {
        handle.getFloat().write(3, value);
    }
    
    /**
     * Retrieve the offset added to the Y position after being multiplied by random.nextGaussian().
     * @return The current Offset Y
    */
    public float getOffsetY() {
        return handle.getFloat().read(4);
    }
    
    /**
     * Set the offset added to the Y position after being multiplied by random.nextGaussian().
     * @param value - new value.
    */
    public void setOffsetY(float value) {
        handle.getFloat().write(4, value);
    }
    
    /**
     * Retrieve the offset added to the Z position after being multiplied by random.nextGaussian().
     * @return The current Offset Z
    */
    public float getOffsetZ() {
        return handle.getFloat().read(5);
    }
    
    /**
     * Set offset added to the Z position after being multiplied by random.nextGaussian().
     * @param value - new value.
    */
    public void setOffsetZ(float value) {
        handle.getFloat().write(5, value);
    }
    
    /**
     * Retrieve the speed of each particle.
     * @return The current particle speed
    */
    public float getParticleSpeed() {
        return handle.getFloat().read(6);
    }
    
    /**
     * Set the speed of each particle.
     * @param value - new speed.
    */
    public void setParticleSpeed(float value) {
        handle.getFloat().write(6, value);
    }
    
    /**
     * Retrieve the number of particles to create.
     * @return The current number of particles
    */
    public int getNumberOfParticles() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set the number of particles to create.
     * @param value - new count.
    */
    public void setNumberOfParticles(int value) {
        handle.getIntegers().write(0, value);
    }
}