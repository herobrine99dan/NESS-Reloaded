package com.github.ness.packets.event;

import com.github.ness.NessPlayer;
import com.github.ness.packets.Packet;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity.EntityUseAction;
import lombok.Getter;

public class UseEntityEvent extends ReceivedPacketEvent {
    
    @Getter
    private int entityId = 0;
    @Getter
    private EntityUseAction action;
    @Getter
    private WrappedPacketInUseEntity useEntityPacket;

    public UseEntityEvent(NessPlayer nessplayer, Packet packet) {
        super(nessplayer, packet);
    }

    @Override
    public void process() {
        useEntityPacket = new WrappedPacketInUseEntity(this.getPacket().getRawPacket());
        this.entityId = useEntityPacket.getEntityID();
        action = useEntityPacket.getAction();
    }
}
