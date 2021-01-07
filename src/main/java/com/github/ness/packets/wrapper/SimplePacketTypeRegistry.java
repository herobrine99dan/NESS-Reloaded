package com.github.ness.packets.wrapper;

import com.github.ness.packets.PacketType;
import com.github.ness.reflect.ReflectHelper;

import java.util.HashMap;
import java.util.Map;

public final class SimplePacketTypeRegistry extends PacketTypeRegistry {

	private final Map<Class<?>, PacketType<?>> packetTypesMap;

	public SimplePacketTypeRegistry(Map<Class<?>, PacketType<?>> packetTypesMap) {
		this.packetTypesMap = packetTypesMap;
	}

	public SimplePacketTypeRegistry(ReflectHelper helper) {
		this(buildPacketTypesMap(helper));
	}

	private static Map<Class<?>, PacketType<?>> buildPacketTypesMap(ReflectHelper helper) {
		Map<Class<?>, PacketType<?>> packetTypesMap = new HashMap<>();
		packetTypesMap.put(PlayInFlying.class, PlayInFlying.type(helper));
		return packetTypesMap;
	}

	@Override
	protected <P> PacketType<P> getPacketType(Class<P> packetTypeClass) {
		@SuppressWarnings("unchecked")
		PacketType<P> type = (PacketType<P>) packetTypesMap.get(packetTypeClass);
		return type;
	}

}
