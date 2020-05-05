/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.github.ness.tinyprotocol.packet.in;

import com.github.ness.tinyprotocol.api.NMSObject;
import com.github.ness.tinyprotocol.api.ProtocolVersion;
import com.github.ness.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInFlyingPacket extends NMSObject {
    private static final String packet = Client.FLYING;

    // Fields
    private static FieldAccessor<Double> fieldX = fetchField(packet, double.class, 0);
    private static FieldAccessor<Double> fieldY = fetchField(packet, double.class, 1);
    private static FieldAccessor<Double> fieldZ = fetchField(packet, double.class, 2);
    private static FieldAccessor<Float> fieldYaw = fetchField(packet, float.class, 0);
    private static FieldAccessor<Float> fieldPitch = fetchField(packet, float.class, 1);
    private static FieldAccessor<Boolean> fieldGround = fetchField(packet, boolean.class, 0);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;
    private boolean look, pos, ground;

    public WrappedInFlyingPacket(Object packet) {
        super(packet);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        String name = getPacketName();
        // This saves up 2 reflection calls
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            pos = name.equals(Client.LEGACY_POSITION) || name.equals(Client.LEGACY_POSITION_LOOK);
            look = name.equals(Client.LEGACY_LOOK) || name.equals(Client.LEGACY_POSITION_LOOK);
        } else {
            pos = name.equals(Client.POSITION) || name.equals(Client.POSITION_LOOK);
            look = name.equals(Client.LOOK) || name.equals(Client.POSITION_LOOK);
        }
        if (pos) {
            x = fetch(fieldX);
            y = fetch(fieldY);
            z = fetch(fieldZ);
        }
        if (look) {
            yaw = fetch(fieldYaw);
            pitch = fetch(fieldPitch);
        }
        ground = fetch(fieldGround);
    }
}
