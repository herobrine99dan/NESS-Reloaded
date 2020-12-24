package com.github.ness.check;

import com.github.ness.NessPlayer;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PacketTypeRegistry;
import com.github.ness.reflect.ReflectHelper;

/**
 * A check which listens to packets
 *
 */
public abstract class PacketCheck extends Check {

	protected PacketCheck(PacketCheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}

	@Override
	public PacketCheckFactory<?> getFactory() {
		return (PacketCheckFactory<?>) super.getFactory();
	}

	/**
	 * Checks a packet. The packet will always be associated with the same player
	 * this check applies to.
	 *
	 * @param packet the packet
	 */
	protected abstract void checkPacket(Packet packet);

	void checkPacketUnlessInvalid(Packet packet) {
		if (player().isInvalid()) {
			return;
		}
		checkPacket(packet);
	}

	protected PacketTypeRegistry packetTypeRegistry() {
		return ness().getPacketTypeRegistry();
	}

	protected ReflectHelper reflectHelper() {
		return ness().getReflectHelper();
	}

}
