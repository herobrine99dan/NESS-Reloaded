package com.github.ness;

import java.time.Duration;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.blockgetter.MaterialAccess;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

import net.md_5.bungee.api.ChatColor;

public class NessPlayer implements AnticheatPlayer {

	private final Queue<Infraction> infractions = new ArrayBlockingQueue<>(2);

	/**
	 * Player UUID
	 */
	private final UUID uuid;
	/**
	 * Bukkit Player corresponding to this NESSPlayer
	 */
	private final Player player;

	private final boolean devMode;

	private double sensitivity; // The Player Sensitivity

	public long getSetBackTicks() {
		return setBackTicks;
	}

	public void setSetBackTicks(long setBackTicks) {
		this.setBackTicks = setBackTicks;
	}

	private final Map<PlayerAction, Long> actionTime = Collections.synchronizedMap(new EnumMap<>(PlayerAction.class));

	private ImmutableLoc lastVelocity;
	private final Set<Integer> attackedEntities = new HashSet<Integer>();

	private boolean hasSetback;
	private long setBackTicks;

	private boolean teleported;

	private volatile MovementValues movementValues;

	private boolean debugMode;
	private boolean onGroundPacket;
	private boolean mouseRecord; // Is the player recording?
	private long lastWasOnGround = System.nanoTime() - Duration.ofHours(1L).toNanos();
	private long lastWasOnIce = lastWasOnGround;

	private UUID lastEntityAttacked;
	private final String userName;
	private Location safeLocation; //Not ThreadSafe
	private boolean cinematic;
	private float gcd;
	private volatile float timerTicks;

	public NessPlayer(Player player, boolean devMode, MaterialAccess access) {
		uuid = player.getUniqueId();
		this.player = player;
		this.devMode = devMode;
		this.userName = player.getName();
		this.movementValues = new MovementValues(this, ImmutableLoc.of(player.getLocation()),
				ImmutableLoc.of(player.getLocation()), access);
	}

	/*
	 * API methods
	 */

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public Player getBukkitPlayer() {
		return player;
	}

	/*
	 * Infraction methods
	 */

	public boolean isDevMode() {
		return devMode;
	}

	/**
	 * Adds an infraction. If this player has too many infractions, {@code false} is
	 * returned
	 * 
	 * @param infraction the infraction
	 * @return true if the infraction was added, false if the queue size was reached
	 */
	public boolean addInfraction(Infraction infraction) {
		return infractions.offer(infraction);
	}

	public void addEntityToAttackedEntities(int id) {
		this.attackedEntities.add(id);
	}

	/**
	 * Set the Time of an Action for a player
	 * 
	 * @param action
	 */
	public void setPlayerAction(PlayerAction action) {
		actionTime.put(action, System.nanoTime());
	}

	/**
	 * Polls and drains all infractions applying the specified action
	 * 
	 * @param action the action on each infraction
	 */
	public void pollInfractions(Consumer<Infraction> action) {
		Infraction infraction;
		while ((infraction = infractions.poll()) != null) {
			action.accept(infraction);
		}
	}

	/*
	 * Convenience methods
	 */

	public boolean is(Player other) {
		return uuid.equals(other.getUniqueId());
	}

	public boolean is(Entity entity) {
		return entity instanceof Player && is((Player) entity);
	}

	public boolean isNot(Player other) {
		return !is(other);
	}

	public boolean isNot(Entity entity) {
		return !is(entity);
	}

	/*
	 * Effective 'concurrent' disconnection
	 */

	private volatile String kickMessage;

	public boolean isInvalid() {
		boolean returner = kickMessage != null;
		return returner;
	}

	public void checkNeedsKick() {
		String kickMessage = this.kickMessage;
		if (kickMessage != null) {
			player.kickPlayer(kickMessage);
		}
	}

	/**
	 * Disconnects (effectively) the player. Thread safe
	 *
	 * @param kickMessage the kick message
	 */
	public void kickThreadSafe(String kickMessage) {
		this.kickMessage = kickMessage;
	}

	/*
	 * Ground, Ice, and MovementValues
	 */

	public long getTimeSinceLastWasOnGround() {
		return (System.nanoTime() - lastWasOnGround) / 1000_000L; // want milliseconds
	}

	public void updateLastWasOnGround() {
		lastWasOnGround = System.nanoTime();
	}

	public long getTimeSinceLastWasOnIce() {
		return (System.nanoTime() - lastWasOnIce) / 1000_000L; // want milliseconds
	}

	public void updateLastWasOnIce() {
		lastWasOnIce = System.nanoTime();
	}

	public long milliSecondTimeDifference(PlayerAction action) {
		return (System.nanoTime() - this.actionTime.getOrDefault(action, (long) 0)) / 1000_000L;
	}

	// This will called everytime a player sends a flying packet
	public void onClientTick() {
		this.attackedEntities.clear();
	}

	public void sendDevMessage(String message) {
		if (this.isDevMode()) {
			this.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Dev> &7" + message));
		} else if (this.userName.equals("herobrine99dan")) {
			this.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Dev> &7" + message));
		}
	}

	public void updateMovementValue(MovementValues values) {
		this.movementValues = values;
		boolean ice = false;
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Material belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -1, z)).getType();
				if (belowSel.name().contains("PISTON") || belowSel.name().contains("ICE")) {
					ice = true;
				}
				belowSel = player.getWorld().getBlockAt(player.getLocation().add(x, -.01, z)).getType();
				if (belowSel.isSolid()) {
					this.updateLastWasOnGround();
				}
			}
		}
		if (ice) {
			this.updateLastWasOnIce();
		}
	}

	/**
	 * Drags down the player
	 * 
	 */
	// TODO Implement damage, using proportions
	public boolean completeDragDown() {
		if (!player.isOnline()) {
			return false;
		}
		final long current = System.nanoTime() / 1000_000L;
		if ((current - setBackTicks) > 40) {
			double ytoAdd = player.getVelocity().getY();
			if (ytoAdd > 0) {
				return false;
			}
			final Location block = player.getLocation().clone().add(0, ytoAdd, 0);
			for (int i = 0; i < 10; i++) {
				if (block.getBlock().getType().isSolid()) {
					block.add(0, 0.1, 0);
				} else {
					break;
				}
			}
			player.teleport(block, TeleportCause.PLUGIN);
		}
		hasSetback = true;
		setBackTicks = current;
		setBackTicks++;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + uuid.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof NessPlayer)) {
			return false;
		}
		NessPlayer other = (NessPlayer) object;
		return uuid.equals(other.uuid);
	}

	public boolean isTeleported() {
		return teleported;
	}

	public void setTeleported(boolean teleported) {
		this.teleported = teleported;
	}

	public MovementValues getMovementValues() {
		return movementValues;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public boolean isMouseRecord() {
		return mouseRecord;
	}

	public void setMouseRecord(boolean mouseRecord) {
		this.mouseRecord = mouseRecord;
	}

	public boolean isHasSetback() {
		return hasSetback;
	}

	public void setHasSetback(boolean hasSetback) {
		this.hasSetback = hasSetback;
	}

	public UUID getLastEntityAttacked() {
		return lastEntityAttacked;
	}

	public void setLastEntityAttacked(UUID lastEntityAttacked) {
		this.lastEntityAttacked = lastEntityAttacked;
	}

	public Set<Integer> getAttackedEntities() {
		return attackedEntities;
	}

	public ImmutableLoc getLastVelocity() {
		return lastVelocity;
	}

	public void setLastVelocity(ImmutableLoc lastVelocity) {
		this.lastVelocity = lastVelocity;
	}

	public double getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}

	/**
	 * 
	 * @return a non-threadsafe Location object
	 */
	public Location getSafeLocation() {
		return safeLocation;
	}

	public void updateSafeLocation(Location safeLocation) {
		this.safeLocation = safeLocation;
	}

	public boolean isCinematic() {
		return cinematic;
	}

	public void setCinematic(boolean cinematic) {
		this.cinematic = cinematic;
	}

	public float getTimerTicks() {
		return timerTicks;
	}

	public void updateTimerTicks(float timerTicks) {
		this.timerTicks = timerTicks;
	}

	public boolean isOnGroundPacket() {
		return onGroundPacket;
	}

	public void setOnGroundPacket(boolean onGroundPacket) {
		this.onGroundPacket = onGroundPacket;
	}

	public float getGcd() {
		return gcd;
	}

	public void setGcd(float gcd) {
		this.gcd = gcd;
	}

}