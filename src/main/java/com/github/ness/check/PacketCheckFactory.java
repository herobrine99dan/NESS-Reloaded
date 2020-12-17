package com.github.ness.check;

import com.github.ness.packets.Packet;

/**
 * Factory for packet checks
 *
 * @param <C> the type of the check
 */
public class PacketCheckFactory<C extends PacketCheck> extends CheckFactory<C> {

	protected PacketCheckFactory(CheckInstantiator<C> instantiator, String checkName, CheckManager manager,
								 CheckInfo checkInfo) {
		super(instantiator, checkName, manager, checkInfo);
	}

	void receivePacket(Packet packet) {
		C check = getChecksMap().get(packet.getAssociatedUniqueId());
		if (check != null) {
			check.checkPacketUnlessInvalid(packet);
		}
	}

}
