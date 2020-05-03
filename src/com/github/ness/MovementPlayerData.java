package com.github.ness;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementPlayerData {
	private Player player;
	double StrafeValuex = 0.0;
	int FlyMoves = 0;
	public float suspiciousYaw = 0.0f;
	double StrafeValuez = 0.0;
	long SprintLastToggle = 0;
	public double DistanceFastStairs = 0.0;
	public long pingspooftimer = 0;
	public long oldpingspooftimer = 0;
	private double LastYDIff = 0.0;
	private HashMap<String, Double> distance = new HashMap<String, Double>();
	private Map<String, Location> onground = new HashMap<String, Location>();
	private static Map<String, MovementPlayerData> nessplayers = new HashMap<String, MovementPlayerData>();

	private MovementPlayerData(Player player) {
		this.player = player;
		nessplayers.put(player.getName(), this);
	}

	// Return a running instance (or create a new one)
	public static MovementPlayerData getInstance(Player player) {
		if (nessplayers == null) {
			return new MovementPlayerData(player);
		}
		if (!nessplayers.containsKey(player.getName())) {
			return new MovementPlayerData(player);
		} else if (nessplayers.containsKey(player.getName())) {
			return nessplayers.get(player.getName());
		} else {
			return null;
		}
	}

	// Remove an Instance
	public static boolean removeInstance(Player player) {
		if (nessplayers.containsKey(player.getName())) {
			nessplayers.remove(player.getName());
			return true;
		} else {
			return false;
		}
	}

	// Your special (non-static) methods defined here:
	public void setDistance(double dist) {
		distance.put(this.player.getName(), dist);
	}

	public Location getOnGroundLocation() {
		return this.onground.getOrDefault(this.player.getName(), this.player.getLocation());
	}

	public void setValuesMovement(PlayerMoveEvent e) {
		this.setDistance(Utility.getMaxSpeed(e.getFrom(), e.getTo()));
		Player p = e.getPlayer();// Double.valueOf(Float.valueOf(e.getFrom().getYaw() -
									// e.getTo().getYaw()).toString()).doubleValue() if (Utility.isOnGround(p) &&
									// Utilities.isLocationOnGround(p.getLocation())) {
		this.onground.put(p.getName(), p.getLocation());
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setStrafeValueX(double diffX) {
		StrafeValuex = diffX;
	}

	public void setStrafeValueZ(double diffZ) {
		StrafeValuez = diffZ;
	}

	public double getStrafeValueX() {
		return StrafeValuex;
	}

	public double getStrafeValueZ() {
		return StrafeValuez;
	}

	public int getFlyMoves() {
		return FlyMoves;
	}

	public void setFlyMoves(int flyMoves) {
		FlyMoves = flyMoves;
	}

	public double getLastYDiff() {
		return FlyMoves;
	}

	public void setLastYDiff(double x) {
		LastYDIff = x;
	}

	public long getSprintLastToggle() {
		return SprintLastToggle;
	}

	public void setSprintLastToggle(long n) {
		SprintLastToggle = n;
	}

}