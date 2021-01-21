package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class InvalidDirection extends PacketCheck {

    public static final CheckInfo checkInfo = CheckInfos.forPackets();

    public InvalidDirection(final PacketCheckFactory<?> factory, final NessPlayer nessPlayer) {
        super(factory, nessPlayer);
    }

    @Override
    protected void checkPacket(Packet packet) {
        if (!packet.isPacketType(packetTypeRegistry().playInFlying())) return;

        final PlayInFlying wrapper = packet.toPacketWrapper(packetTypeRegistry().playInFlying());

        if (Math.abs(wrapper.pitch()) > 90.0f) {
            flag();
        }
    }
}
