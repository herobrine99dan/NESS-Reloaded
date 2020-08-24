package com.github.ness.packets.wrappers;

import com.github.ness.utility.ReflectionUtility;

public class SimplePacket {
	private Object packet;

	public SimplePacket(Object packet) {
		this.packet = packet;
	}

	public String getName() {
		return this.packet.getClass().getSimpleName();
	}

	public Object getPacket() {
		return this.packet;
	}
	
	/**
	 * This method cast this object to the PacketPlayInPositionLook Object
	 * Return Null if this object isn't a PacketPlayInPositionLook Object
	 * @return
	 */
	public PacketPlayInPositionLook getPositionPacket() {
		if(this instanceof PacketPlayInPositionLook) {
			return (PacketPlayInPositionLook) this;
		}else {
			return null;
		}
	}
	
	/**
	 * This method cast this object to the PacketPlayInUseEntity Object
	 * Return Null if this object isn't a PacketPlayInUseEntity Object
	 * @return
	 */
	public PacketPlayInUseEntity getUseEntity() {
		if(this instanceof PacketPlayInUseEntity) {
			return (PacketPlayInUseEntity) this;
		}else {
			return null;
		}
	}
	
	public Object getField(String field) {
		return ReflectionUtility.getField(packet, field);
	}
	
	public Object getDeclaredField(String field) {
		return ReflectionUtility.getDeclaredField(packet, field);
	}

}
