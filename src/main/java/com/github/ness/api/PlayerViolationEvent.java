package com.github.ness.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.ness.NessPlayer;

import lombok.Getter;
import lombok.Setter;

public class PlayerViolationEvent extends Event implements Cancellable {

	@Getter
	private final Player player;
	@Getter
	private final NessPlayer nessplayer;
	@Getter
	private final int violations;
	@Getter
	private final Violation violation;
	@Setter
	private boolean cancelled;

	/**
	 * This event is fired when someone get a violation This event can be
	 * Asynchronously (For Example In Some Packets Checks) and Synchronous (In All
	 * Other Checks)
	 */

	public PlayerViolationEvent(Player player, NessPlayer nessplayer, Violation violation, int violations) {
		super(!Bukkit.isPrimaryThread());
		this.player = player;
		this.nessplayer = nessplayer;
		this.violation = violation;
		this.violations = violations;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}
}
