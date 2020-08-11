package com.github.ness.packets.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.ness.NessPlayer;
import com.github.ness.packets.wrappers.SimplePacket;

import lombok.Getter;

public class ReceivedPacketEvent extends Event implements Cancellable {
	private boolean cancelled;
	@Getter
	SimplePacket packet;
	@Getter
	NessPlayer nessPlayer;

	private static final HandlerList HANDLERS = new HandlerList();

	public ReceivedPacketEvent(NessPlayer nessplayer, SimplePacket packet) {
		super(!Bukkit.isPrimaryThread());
		this.packet = packet;
		this.nessPlayer = nessplayer;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
