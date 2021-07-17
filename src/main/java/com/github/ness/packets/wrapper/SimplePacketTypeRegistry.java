package com.github.ness.packets.wrapper;

import java.util.HashMap;
import java.util.Map;

import com.github.ness.packets.PacketType;
import com.github.ness.reflect.ReflectHelper;
import com.github.ness.reflect.locator.VersionDetermination;

public final class SimplePacketTypeRegistry extends PacketTypeRegistry {

	private final Map<Class<?>, PacketType<?>> packetTypesMap;

	public SimplePacketTypeRegistry(Map<Class<?>, PacketType<?>> packetTypesMap) {
		this.packetTypesMap = packetTypesMap;
	}

	public SimplePacketTypeRegistry(VersionDetermination version, ReflectHelper helper) {
		this(buildPacketTypesMap(version, helper));
	}

	private static Map<Class<?>, PacketType<?>> buildPacketTypesMap(VersionDetermination version, ReflectHelper helper) {
		Map<Class<?>, PacketType<?>> packetTypesMap = new HashMap<>();
		packetTypesMap.put(PlayInFlying.class, PlayInFlying.type(helper));
		packetTypesMap.put(PlayInEntityAction.class, PlayInEntityAction.type(version, helper));
		packetTypesMap.put(PlayInArmAnimation.class, PlayInArmAnimation.type(helper));
		packetTypesMap.put(PlayInUseEntity.class, PlayInUseEntity.type(version, helper));
		return packetTypesMap;
	}

	@Override
	protected <P> PacketType<P> getPacketType(Class<P> packetTypeClass) {
		@SuppressWarnings("unchecked")
		PacketType<P> type = (PacketType<P>) packetTypesMap.get(packetTypeClass);
		return type;
	}

}
