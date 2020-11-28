package com.github.ness.packets.event;

import com.github.ness.NessPlayer;
import com.github.ness.packets.Packet;

import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import lombok.Getter;

public class FlyingEvent extends ReceivedPacketEvent {
    @Getter
    float pitch = 0;
    @Getter
    double x = 0;
    @Getter
    double y = 0;
    @Getter
    float yaw = 0;
    @Getter
    double z = 0;
    @Getter
    boolean look = false;
    @Getter
    boolean onGround = false;
    @Getter
    boolean position = false;

    public FlyingEvent(NessPlayer nessplayer, Packet packet) {
        super(nessplayer, packet);
    }

    @Override
    public void process() {
        WrappedPacketInFlying packet = new WrappedPacketInFlying(this.getPacket().getRawPacket());
        if (packet.isLook()) {
            this.pitch = packet.getPitch();
            this.yaw = packet.getYaw();
        }
        if (packet.isPosition()) {
            this.x = packet.getX();
            this.y = packet.getY();
            this.z = packet.getZ();
        }
        this.look = packet.isLook();
        this.position = packet.isPosition();
        this.onGround = packet.isOnGround();
    }
}
