package com.github.ness.packets;

import com.github.ness.NessPlayer;
import com.github.ness.packets.wrapper.SimplePacket;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReceivedPacketEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final SimplePacket packet;
	private final NessPlayer nessPlayer;
	private boolean cancelled;

	public ReceivedPacketEvent(NessPlayer nessplayer, SimplePacket packet) {
		super(!Bukkit.isPrimaryThread());
		this.packet = packet;
		this.nessPlayer = nessplayer;
	}

	public SimplePacket getPacket() {
		return packet;
	}

	public NessPlayer getNessPlayer() {
		return nessPlayer;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
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
}
