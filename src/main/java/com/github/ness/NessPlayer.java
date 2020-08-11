package com.github.ness;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.api.PlayerViolationEvent;
import com.github.ness.api.Violation;

import lombok.Getter;
import lombok.Setter;

public class NessPlayer implements AutoCloseable {

	/**
	 * Bukkit Player corresponding to this NessPlayer
	 * 
	 */
	@Getter
	private final Player player;

	@Getter
	@Setter
	private boolean teleported;

	/**
	 * Player's current violation, package visibility for ViolationManager to use
	 * 
	 */
	final AtomicReference<Violation> violation = new AtomicReference<>();
	/**
	 * Used by ViolationManager to count violations of specific checks. <br>
	 * This map is synchronized and thread safe
	 * 
	 */
	public final Map<String, Integer> checkViolationCounts = Collections.synchronizedMap(new HashMap<>());

	@Getter
	@Setter
	public double lastStrafeAngle; //For the Beta NewOldStrafe Check
	public int blockPlace; // For FastPlace Check
	public int normalPacketsCounter; // For MorePackets
	public int CPS; // For AutoClicker
	public float lastPitch; // Used in GhostHand
	public float lastYaw;
	public int AimbotPatternCounter; // For Aimbot
	public int InvalidVelocitySpeedCounter; // A Counter For Speed Invalid Velocity
	public long lastPacketTime; //Used in BadPackets
	public long movementPackets; //Used in BadPackets
	public double lastStrafeDist; //Used in Strafe
	public float lastStairDist; //Used in BadPackets
	public int noGround; //Used in NoGround Check 
	public int SpeedMaxDistanceViolationsAlert; //Used In MaxDistance Check (I'm applying the same concept which i've used to fix false positives in NoGround Check)
	@Getter
	private MovementValues movementValues;

	// Used in OldMovementChecks

	private long lastWasOnGround = System.nanoTime() - Duration.ofHours(1L).toNanos();

	public long getTimeSinceLastWasOnGround() {
		return (System.nanoTime() - lastWasOnGround) / 1000_000L; // want milliseconds
	}

	public void updateLastWasOnGround() {
		lastWasOnGround = System.nanoTime();
	}

	private long lastWasOnIce = lastWasOnGround;

	public long getTimeSinceLastWasOnIce() {
		return (System.nanoTime() - lastWasOnIce) / 1000_000L; // want milliseconds
	}

	public void updateLastWasOnIce() {
		lastWasOnIce = System.nanoTime();
	}

	// Used for Aimbot check
	public List<Double> pitchDiff = new ArrayList<Double>();
	public double lastGCD = Float.MIN_VALUE;

	// Used for AutoClick check
	@Getter
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();

	private final boolean devMode;

	NessPlayer(Player player, boolean devMode) {
		this.player = player;
		this.teleported = false;
		this.lastPacketTime = 0;
		this.blockPlace = 0;
		this.CPS = 0;
		this.AimbotPatternCounter = 0;
		this.normalPacketsCounter = 0;
		this.lastPitch = 0;
		this.devMode = devMode;
	}

	public void updateMovementValue(MovementValues values) {
		this.movementValues = values;

	}

	public boolean isDevMode() {
		return devMode;
	}

	/**
	 * Gets the player's current, latest violation
	 * 
	 * @return the latest violation
	 */
	public Violation getViolation() {
		return violation.get();
	}

	/**
	 * Used to indicate the player was detected for cheating
	 * 
	 * @param violation the violation
	 */
	public void setViolation(Violation violation) {

		// Violation event
		PlayerViolationEvent event = new PlayerViolationEvent(this.getPlayer(), this, violation,
				checkViolationCounts.getOrDefault(violation.getCheck(), 0));
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		// Bypass permissions
		if (this.getPlayer().hasPermission("ness.bypass." + violation.getCheck().toLowerCase())) {
			return;
		}
		if (this.getPlayer().hasPermission("ness.bypass.*")) {
			return;
		}
		// Main method body
		this.violation.compareAndSet(null, violation);
		checkViolationCounts.merge(violation.getCheck(), 1, (c1, c2) -> c1 + c2);
		if (isDevMode()) {
			// sendMessage is thread safe
			if (this.getPlayer().hasPermission("ness.notify.developer")) {
				player.sendMessage(
						"Dev mode violation: Check " + violation.getCheck() + ". Details: " + violation.getDetails());
			}
		}

		/*
		 * if (player.hasPermission("ness.bypass.*") ||
		 * player.hasPermission("ness.bypass." + violation.getCheck())) { return; } //
		 * player.sendMessage("HACK: " + violation.getCheck() + " Module: " + //
		 * Arrays.toString(violation.getDetails())); NessConfig config =
		 * NESSAnticheat.main.getNessConfig(); ConfigurationSection cs =
		 * config.getViolationHandling().getConfigurationSection("notify-staff");
		 * if(!cs.getBoolean("enable")) { return; } for (Player p :
		 * Bukkit.getOnlinePlayers()) { if (p.hasPermission("ness.notify.hacks")) {
		 * p.sendMessage(cs.getString("notification").replaceFirst("%PLAYER%",
		 * player.getName()) .replaceFirst("%HACK%", violation.getCheck())
		 * .replaceFirst("%DETAILS%", violation.getDetails().toString())); } }
		 */
	}
	
	public boolean shouldCancel(Event e, String check) {
		if(this.isTeleported()) {
			return false;
		}
		ConfigurationSection cancelsec = NESSAnticheat.main.getNessConfig().getViolationHandling()
				.getConfigurationSection("cancel");
		if(!cancelsec.getBoolean("enable")) {
			return false;
		}
		boolean cancel = checkViolationCounts.getOrDefault(check, 0) > cancelsec.getInt("vl", 10);
		if (e instanceof PlayerMoveEvent && cancel) {
			((PlayerMoveEvent) e).setCancelled(true);
		}
		return cancel;
	}

	@Override
	public void close() {
		checkViolationCounts.clear();
	}
}