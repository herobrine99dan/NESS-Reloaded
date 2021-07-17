package com.github.ness.packets.wrapper;

import com.github.ness.packets.PacketType;
import com.github.ness.reflect.FieldInvoker;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.ReflectHelper;
import com.github.ness.reflect.locator.VersionDetermination;

public class PlayInUseEntity {
	
	private final int entityID;
	private final boolean isAttack;

	private PlayInUseEntity(int entityID, boolean isAttack) {
		this.entityID = entityID;
		this.isAttack = isAttack;
	}

	public static PacketType<PlayInUseEntity> type(VersionDetermination version, ReflectHelper helper) {
		Class<?> packetClass = helper.getNmsClass("PacketPlayInUseEntity");
		FieldInvoker<Integer> entityID = helper.getFieldFromNames(packetClass, int.class, "a");
		FieldInvoker<?> enumAction = helper.getField(packetClass, MemberDescriptions.forField("action")); //for 1.12.2 and less
		return new RawPacketType<PlayInUseEntity>(packetClass) {
			@Override
			public PlayInUseEntity convertPacket(Object packet) {
				boolean isAttack = enumAction.get(packet).toString().equals("ATTACK");
				return new PlayInUseEntity(entityID.get(packet), isAttack);
			}
		};
	}

	public int getEntityID() {
		return entityID;
	}

	public boolean isAttack() {
		return isAttack;
	}
	
}
