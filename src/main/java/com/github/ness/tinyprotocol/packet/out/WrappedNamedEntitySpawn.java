/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.github.ness.tinyprotocol.packet.out;

import com.github.ness.tinyprotocol.api.Packet;
import com.github.ness.tinyprotocol.api.ProtocolVersion;
import com.github.ness.tinyprotocol.packet.types.WrappedGameProfile;
import com.github.ness.utility.MathUtils;

import java.util.UUID;

//TODO make this cleaner
public class WrappedNamedEntitySpawn extends Packet {
    private static final String packet = Packet.Server.NAMED_ENTITY_SPAWN;

    public WrappedNamedEntitySpawn(ProtocolVersion version, int id, Object gameProfile, double x, double y, double z, Object dataWatcher, Object watchables) {
        if (version.isBelow(ProtocolVersion.V1_8)) {
            setPacket(packet
                    , id, gameProfile,
                    MathUtils.floor(x * 32.0D),
                    MathUtils.floor(y * 32.0D),
                    MathUtils.floor(z * 32.0D), null, null, null,
                    dataWatcher);
        } else {
            WrappedGameProfile profile = new WrappedGameProfile(gameProfile);
            setPacket(packet
                    , id, profile.getId(), profile.getName(), profile.getPropertyMap(),
                    MathUtils.floor(x * 32.0D),
                    MathUtils.floor(y * 32.0D),
                    MathUtils.floor(z * 32.0D), null, null, null,
                    dataWatcher, watchables);
        }
    }
}
