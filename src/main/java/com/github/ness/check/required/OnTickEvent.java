package com.github.ness.check.required;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInEntityAction;

public class OnTickEvent extends PacketCheck {

	public static final CheckInfo checkInfo = CheckInfos.forPackets();

	public OnTickEvent(PacketCheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory, nessPlayer);
	}

	@Override
	protected void checkPacket(Packet packet) {
		if (packet.isPacketType(packetTypeRegistry().playInFlying())) {
			player().onClientTick();
		}
		if (packet.isPacketType(packetTypeRegistry().playInEntityAction())) {
			PlayInEntityAction entityAction = packet.toPacketWrapper(packetTypeRegistry().playInEntityAction());
			String animation = entityAction.animation().toString();
			this.player().sendDevMessage("animation: " + animation);
			if (animation.equals("PRESS_SHIFT_KEY") || animation.equals("START_SNEAKING")) {
				this.player().getSneaking().set(true);
			} else if (animation.equals("RELEASE_SHIFT_KEY") || animation.equals("STOP_SNEAKING")) {
				this.player().getSneaking().set(false);
			}
			if (animation.equals("START_SPRINTING")) {
				this.player().getSprinting().set(true);
			} else if (animation.equals("STOP_SPRINTING")) {
				this.player().getSprinting().set(false);
			}
		}
	}
}
