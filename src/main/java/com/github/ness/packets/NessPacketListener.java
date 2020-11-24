package com.github.ness.packets;

import com.github.ness.NessAnticheat;
import com.github.ness.packets.Packet.Direction;

import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.annotation.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.event.priority.PacketEventPriority;

public class NessPacketListener extends PacketListenerDynamic {
    NessAnticheat ness;

    public NessPacketListener(NessAnticheat ness) {
        super(PacketEventPriority.LOWEST);
        this.ness = ness;
    }

    @PacketHandler
    public void onPacketReceive(PacketReceiveEvent e) {
        Packet packet = new Packet(Direction.RECEIVE, e.getNMSPacket(), e.getPacketId());
        ReceivedPacketEvent event = new ReceivedPacketEvent(
                ness.getCheckManager().getNessPlayer(e.getPlayer().getUniqueId()), packet);
        ness.getCheckManager().onEvent(event);
    }

    @PacketHandler
    public void onPacketSend(PacketSendEvent event) {
        Packet packet = new Packet(Direction.SEND, event.getNMSPacket(), event.getPacketId());
    }

}
