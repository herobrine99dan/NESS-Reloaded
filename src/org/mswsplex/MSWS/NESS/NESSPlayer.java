package org.mswsplex.MSWS.NESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class NESSPlayer {
	/*
	 * How to use NESSPlayer p = NESSPlayer.getInstance(e.getPlayer());
	 */
	private boolean moved = false;
	private Player player;
	private HashMap<String, Double> distance = new HashMap<String, Double>();
	private HashMap<String, Boolean> isswim = new HashMap<String, Boolean>();
	private HashMap<String, Integer> packets = new HashMap<String, Integer>();
	private HashMap<String, Integer> drops = new HashMap<String, Integer>();
	private HashMap<String, Integer> blockplace = new HashMap<String, Integer>();
	private HashMap<String, Integer> clicks = new HashMap<String, Integer>();
	private HashMap<String, Location> fromlocation = new HashMap<String, Location>();
	private HashMap<String, String> language = new HashMap<String, String>();
	private HashMap<String, Integer> onmoverepeat = new HashMap<String, Integer>();
	private HashMap<String, String> payload = new HashMap<String, String>();
	private HashMap<String, Double> YawDelta = new HashMap<String, Double>();
	private HashMap<String, Double> pitchmcd = new HashMap<String, Double>();
	private HashMap<String, Double> oldY = new HashMap<String, Double>();
	private HashMap<String, Long> fromlong = new HashMap<String, Long>();
	private Map<String, Location> onground = new HashMap<String, Location>();
	private static Map<String, NESSPlayer> nessplayers = new HashMap<String, NESSPlayer>();

	private NESSPlayer(Player player) {
		this.player = player;
		nessplayers.put(player.getName(), this);
	}

	// Return a running instance (or create a new one)
	public static NESSPlayer getInstance(Player player) {
		if (nessplayers == null) {
			return new NESSPlayer(player);
		}
		if (!nessplayers.containsKey(player.getName())) {
			return new NESSPlayer(player);
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
		MovementPlayerData mp = MovementPlayerData.getInstance(e.getPlayer());
		this.setDistance(Utility.getMaxSpeed(e.getFrom(), e.getTo()));
		this.setFromLocation(e.getFrom());
		Player p = e.getPlayer();// Double.valueOf(Float.valueOf(e.getFrom().getYaw() -
									// e.getTo().getYaw()).toString()).doubleValue() if (Utility.isOnGround(p) &&
									// Utilities.isLocationOnGround(p.getLocation())) {
		this.onground.put(p.getName(), p.getLocation());
        if(p.isOnGround()) {
        	mp.setFlyMoves(0);
        } else {
        	mp.setFlyMoves(mp.getFlyMoves() + 1);
        }
	}

	public boolean isMoving() {
		Player p = this.player;
		final Location from = p.getLocation();
		Bukkit.getScheduler().runTaskLater(NESS.main, new Runnable() {
			public void run() {
				Location to = p.getLocation();
				double distance = to.distanceSquared(from);
				if (!(distance == 0.0)) {
					moved = true;
				}
			}
		}, 2L);
		return moved;
	}

	public double getDistance() {
		return distance.getOrDefault(player.getName(), 0.0);
	}

	public void setFromLocation(Location loc) {
		fromlocation.put(this.player.getName(), loc);
		fromlong.put(this.player.getName(), System.currentTimeMillis());
		if (onmoverepeat.get(this.player.getName()) == null) {
			onmoverepeat.put(this.player.getName(), 0);
		}
		onmoverepeat.put(this.player.getName(), onmoverepeat.get(this.player.getName()) + 1);
	}

	public Location getFromLocation() {
		return fromlocation.getOrDefault(player.getName(), player.getLocation());
	}

	public boolean resetOnMoveRepeat() {
		if (onmoverepeat.containsKey(this.player.getName())) {
			onmoverepeat.put(this.player.getName(), 0);
			return true;
		} else {
			return false;
		}
	}

	public int getOnMoveRepeat() {
		if (onmoverepeat.containsKey(this.player.getName())) {
			return onmoverepeat.get(this.player.getName());
		} else {
			return 0;
		}
	}

	public void SetPacketsNumber(int n) {
		packets.put(this.player.getName(), n);
	}

	public int getPacketsNumber() {
		if (packets.containsKey(this.player.getName())) {
			return packets.get(this.player.getName());
		} else {
			return 0;
		}
	}

	public long getFromTime() {
		return fromlong.getOrDefault(player.getName(), System.currentTimeMillis());
	}

	public void SetPayLoad(String n) {
		payload.put(this.player.getName(), n);
	}

	public String getPayLoad() {
		if (payload.containsKey(this.player.getName())) {
			return payload.get(this.player.getName());
		} else {
			return "MC|Brand";
		}
	}

	public void SetLanguage(String n) {
		language.put(this.player.getName(), n);
	}

	public String getLanguage() {
		if (language.containsKey(this.player.getName())) {
			return language.get(this.player.getName());
		} else {
			return "";
		}
	}

	public void SetDrops(int n) {
		drops.put(this.player.getName(), n);
	}

	public int getDrops() {
		return drops.getOrDefault(player.getName(), 1);
	}
	
	public void SetIsSwimming(boolean n) {
		isswim.put(this.player.getName(), n);
	}

	public boolean getIsSwimming() {
		return isswim.getOrDefault(player.getName(), false);
	}

	public void SetClicks(int n) {
		clicks.put(this.player.getName(), n);
	}

	public int getClicks() {
		return clicks.getOrDefault(player.getName(), 1);
	}

	public void SetBlockPlace(int n) {
		blockplace.put(this.player.getName(), n);
	}

	public int getBlockPlace() {
		return blockplace.getOrDefault(player.getName(), 1);
	}
	
	public void SetOldY(double n) {
		oldY.put(this.player.getName(), n);
	}

	public double getOldY() {
		return oldY.getOrDefault(player.getName(), 0.0);
	}

	public void SetPitchMCD(double n) {
		pitchmcd.put(this.player.getName(), n);
	}

	public double getPitchMCD() {
		return pitchmcd.getOrDefault(player.getName(), 0.0);
	}

	public void setYawDelta(double n) {
		this.YawDelta.put(player.getName(), n);
	}

	public double getYawDelta() {
		return YawDelta.getOrDefault(this.player.getName(), 0.0);
	}

	public Player getPlayer() {
		return this.player;
	}

}