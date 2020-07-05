package com.github.ness;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

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
	private boolean teleported = false;

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
	public List<Float> patterns = new ArrayList<Float>();
	@Getter
	@Setter
	double distance = 0.0; // For GhostHand and NoSlowDown
	@Getter
	@Setter
	int clicks = 0; // For FastClick
	@Getter
	@Setter
	int blockplace = 0; // For FastPlace
	@Getter
	@Setter
	int movementpacketscounter = 0; // For BadPackets
	@Getter
	@Setter
	int normalPacketsCounter = 0; // For MorePackets
	@Getter
	@Setter
	int CPS = 0; // For AutoClicker
	@Getter
	@Setter
	long CPSDelay = 0; // For AutoClicker
	@Getter
	@Setter
	long CPSlastDelay = 0; // For AutoClicker
	public float lastPitch = 0; // Used in GhostHand
	public double lastYDelta; // Used in Speed And Fly
	public Location safeLoc; // This should be used to make the better LagBack system
	public int AimbotPatternCounter = 0; // For Aimbot
	public Location lastLocation; // For Killaura
	public double lastDist; // For Strafe Check
	public long lastFlyingPacket;

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
	@Getter
	private List<Float> pitchdelta = new ArrayList<>();
	@Getter
	@Setter
	private float lastmcdpitch = Float.MIN_VALUE;

	// Used for AutoClick check
	@Getter
	private final Set<Long> clickHistory = ConcurrentHashMap.newKeySet();

	private final boolean devMode;

	NessPlayer(Player player, boolean devMode) {
		this.player = player;
		this.lastFlyingPacket = System.currentTimeMillis();
		this.devMode = devMode;
	}

	public void updateMovementValues(PlayerMoveEvent event) {
		this.setDistance(Math.abs(Utility.getMaxSpeed(event.getFrom(), event.getTo())));
	}

	public void updatePacketValues(Object packet) {
		if (packet.toString().toLowerCase().contains("useentity")) {
			lastLocation = this.getPlayer().getLocation().clone();
		}
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
		Random r = new Random();
		if (!r.nextBoolean()) {
			return;
		}
		if (this.getPlayer().hasPermission("ness.bypass." + violation.getCheck().toLowerCase())) {
			return;
		}
		if (this.getPlayer().hasPermission("ness.bypass.*")) {
			return;
		}
		this.violation.compareAndSet(null, violation);
		checkViolationCounts.merge(violation.getCheck(), 1, (c1, c2) -> c1 + c2);
		if (isDevMode()) {
			// sendMessage is thread safe
			if(this.getPlayer().hasPermission("ness.notify.developer")) {
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

	@Override
	public void close() {

	}

	public boolean shouldCancel(Event e, String check) {
		ConfigurationSection cancelsec = NESSAnticheat.main.getNessConfig().getViolationHandling()
				.getConfigurationSection("cancel");
		boolean cancel = checkViolationCounts.getOrDefault(check, 0) > cancelsec.getInt("vl", 10);
		if (e instanceof PlayerMoveEvent && cancel) {
			((PlayerMoveEvent) e).setCancelled(true);
		}
		return cancel;
	}

}