package com.github.ness.api;

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

	public PlayerViolationEvent(Player player, NessPlayer nessplayer, Violation violation, int violations) {
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
