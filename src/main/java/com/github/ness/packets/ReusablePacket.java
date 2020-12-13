package com.github.ness.packets;

import java.util.UUID;

import com.github.ness.reflect.Reflection;

class ReusablePacket implements Packet {

	private final Reflection reflection;

	private Object rawPacket;
	private UUID associatedUniqueId;
	private boolean cancelled;

	ReusablePacket(Reflection reflection) {
		this.reflection = reflection;
	}

	@Override
	public Object getRawPacket() {
		return rawPacket;
	}

	@Override
	public UUID getAssociatedUniqueId() {
		return associatedUniqueId;
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	void changeTo(UUID associatedUniqueId, Object rawPacket) {
		this.associatedUniqueId = associatedUniqueId;
		this.rawPacket = rawPacket;
		cancelled = false;
	}

	boolean isCancelled() {
		return cancelled;
	}

}
