package com.github.ness.check;

import com.github.ness.NessPlayer;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PacketTypeRegistry;
import com.github.ness.reflect.ReflectHelper;
import com.github.ness.violation.ViolationManager;

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
	
	/**
	 * Flags the player for cheating, and cancels the event if the violation count is too high (when configured)
	 * 
	 * @param evt the event to cancel
	 * @param details debugging details
	 */
	protected final void flagEvent(Packet evt, String details) {
		if (callFlagEvent()) {
			int violations = flag0(details).getCount();
			ViolationManager violationManager = getFactory().getCheckManager().getNess().getViolationManager();
			if (violationManager.shouldCancelPacketCheck(this, violations)) {
				evt.cancel();
			}
		}
	}

	protected PacketTypeRegistry packetTypeRegistry() {
		return ness().getPacketTypeRegistry();
	}

	protected ReflectHelper reflectHelper() {
		return ness().getReflectHelper();
	}

}
