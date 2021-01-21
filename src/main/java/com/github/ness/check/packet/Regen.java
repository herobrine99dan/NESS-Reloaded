package com.github.ness.check.packet;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfo;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.PacketCheck;
import com.github.ness.check.PacketCheckFactory;
import com.github.ness.packets.Packet;
import com.github.ness.packets.wrapper.PlayInFlying;

public class Regen extends PacketCheck {

    public static final CheckInfo checkInfo = CheckInfos.forPackets();

    private int streak;

    public Regen(final PacketCheckFactory<?> factory, final NessPlayer nessPlayer) {
        super(factory, nessPlayer);
    }

    @Override
    protected void checkPacket(Packet packet) {
        if (!packet.isPacketType(packetTypeRegistry().playInFlying())) return;

        final PlayInFlying wrapper = packet.toPacketWrapper(packetTypeRegistry().playInFlying());

        // The client will update its position every 20 ticks. Some regen modules will flag this since they just
        // spam flying packets without updating their position.

        if (wrapper.hasPosition() || player().getBukkitPlayer().isInsideVehicle()) {
            streak = 0;
            return;
        }

        if (++streak > 20) {
            flag();
        }
    }
}
