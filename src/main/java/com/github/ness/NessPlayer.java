package com.github.ness;

import java.awt.Point;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.md_5.bungee.api.ChatColor;

import lombok.Getter;
import lombok.Setter;

public class NessPlayer implements AnticheatPlayer {
	
	private final Queue<Infraction> infractions = new ConcurrentLinkedQueue<>();

	/**
	 * Player UUID
	 */
	private final UUID uuid;
	/**
	 * Bukkit Player corresponding to this NESSPlayer
	 */
	private final Player player;
	@Getter
	private final boolean devMode;
	public double sensitivity; // The Player Sensitivity
	final public Map<PlayerAction, Long> actionTime;
	public List<Point> mouseRecordValues;
	public ImmutableLoc velocity;
	public Set<Integer> attackedEntities;
	public boolean hasSetback;
	long setBackTicks;
	@Getter
	@Setter
	private boolean teleported;
	@Getter
	private volatile MovementValues movementValues;
	@Getter
	@Setter
	private boolean debugMode;
	@Getter
	@Setter
	private boolean mouseRecord; // Is the player recording?
	private long lastWasOnGround = System.nanoTime() - Duration.ofHours(1L).toNanos();
	private long lastWasOnIce = lastWasOnGround;

	public NessPlayer(Player player, boolean devMode) {
		uuid = player.getUniqueId();
		this.player = player;
		this.teleported = false;
		this.setBackTicks = 0;
		this.mouseRecordValues = new ArrayList<Point>();
		this.actionTime = Collections.synchronizedMap(new EnumMap<>(PlayerAction.class));
		this.sensitivity = 0;
		this.devMode = devMode;
		this.attackedEntities = new HashSet<Integer>();
		this.movementValues = new MovementValues(player,
				new ImmutableLoc(player.getWorld().getName(), 0d, 0d, 0d, 0f, 0d),
				new ImmutableLoc(player.getWorld().getName(), 0d, 0d, 0d, 0f, 0d));
	}
	
	@Override
	public UUID getUniqueId() {
		return uuid;
	}
	
	@Override
	public Player getBukkitPlayer() {
		return player;
	}
	
	/**
	 * Adds an infraction
	 * 
	 * @param infraction the infraction
	 */
	public void addInfraction(Infraction infraction) {
		infractions.offer(infraction);
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

	public long nanoTimeDifference(PlayerAction action) {
		return (System.nanoTime() - this.actionTime.getOrDefault(action, (long) 0)) / 1000_000L;
	}

	// This will called everytime a player sends a flying packet
	public void onClientTick() {
		this.attackedEntities.clear();
	}

	public void sendDevMessage(String message) {
		this.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Dev> &7" + message));
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
	public void completeDragDown() {
		if (!player.isOnline()) {
			return;
		}
		final long current = System.nanoTime() / 1000_000L;
		if ((current - setBackTicks) > 40) {
			final Location block = player.getLocation().clone().add(0, player.getVelocity().getY(), 0);
			if (!block.getBlock().getType().isSolid()) {
				hasSetback = true;
				player.teleport(block, TeleportCause.PLUGIN);
			} else if (!block.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
				player.teleport(block.add(0, 0.4, 0), TeleportCause.PLUGIN);
			}
		}
		setBackTicks = current;
		setBackTicks++;
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

}