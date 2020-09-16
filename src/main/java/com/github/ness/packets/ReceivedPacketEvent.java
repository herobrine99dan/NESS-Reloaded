package com.github.ness.packets;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.ness.NESSPlayer;
import com.github.ness.packets.wrappers.SimplePacket;

import lombok.Getter;

public class ReceivedPacketEvent extends Event implements Cancellable {
	private boolean cancelled;
	@Getter
	SimplePacket packet;
	@Getter
	NESSPlayer nessPlayer;

	private static final HandlerList HANDLERS = new HandlerList();

	public ReceivedPacketEvent(NESSPlayer nessplayer, SimplePacket packet) {
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
