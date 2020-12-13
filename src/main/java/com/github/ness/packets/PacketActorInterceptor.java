package com.github.ness.packets;

import java.util.UUID;

import com.github.ness.reflect.Reflection;

public class PacketActorInterceptor implements PacketInterceptor {

	private final PacketActor actor;
	private final ThreadLocal<ReusablePacket> threadLocalPacket;

	public PacketActorInterceptor(PacketActor actor, Reflection reflection) {
		this.actor = actor;
		threadLocalPacket = ThreadLocal.withInitial(() -> new ReusablePacket(reflection));
	}

	@Override
	public boolean shouldDrop(UUID uuid, Object rawPacket) throws Exception {
		ReusablePacket packet = threadLocalPacket.get();
		packet.changeTo(uuid, rawPacket);
		actor.onPacket(packet);
		return packet.isCancelled();
	}

}
