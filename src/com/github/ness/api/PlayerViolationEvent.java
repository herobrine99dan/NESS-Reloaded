package com.github.ness.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.mswsplex.MSWS.NESS.NESSPlayer;

public class PlayerViolationEvent extends Event implements Cancellable {
	private final String playerName;
	private final String cheat;
	private final String module;
	private final int vl;
	private boolean isCancelled;

	public PlayerViolationEvent(String playerName, String hack, int violations, String module) {
		this.vl = violations;
		this.cheat = hack;
		this.module = module;
		this.playerName = playerName;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public boolean isCancelled() {
		return this.isCancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(playerName);
	}
	
	public NESSPlayer getNESSPlayer() {
		NESSPlayer p = NESSPlayer.getInstance(Bukkit.getPlayer(playerName));
		return p;
	}

	public String getHack() {
		return this.cheat;
	}

	public int getVl() {
		return this.vl;
	}

	public String getModule() {
		return this.module;
	}
}
