package com.github.ness.tinyprotocol.packet.in;

import com.github.ness.tinyprotocol.api.NMSObject;
import com.github.ness.tinyprotocol.api.ProtocolVersion;
import com.github.ness.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInKeepAlivePacket extends NMSObject {
    private static final String packet = Client.KEEP_ALIVE;

    private static FieldAccessor<Integer> fieldLegacy = fetchField(packet, int.class, 0);
    private static FieldAccessor<Long> field = fetchField(packet, long.class, 0);

    private long time;

    public WrappedInKeepAlivePacket(Object packet) {
        super(packet);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) time = fetch(fieldLegacy);
        else time = fetch(field);
    }
}
