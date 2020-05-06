package com.github.ness.tinyprotocol.packet.in;

import com.github.ness.tinyprotocol.api.NMSObject;
import com.github.ness.tinyprotocol.api.ProtocolVersion;
import com.github.ness.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInEntityActionPacket extends NMSObject {
    private static final String packet = Client.ENTITY_ACTION;

    // Fields
    private static FieldAccessor<Enum> fieldAction = fetchField(packet, Enum.class, 0);

    // Decoded data
    private EnumPlayerAction action;

    public WrappedInEntityActionPacket(Object packet) {
        super(packet);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        action = EnumPlayerAction.values()[fetch(fieldAction).ordinal()];
    }

    public enum EnumPlayerAction {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        RIDING_JUMP,
        OPEN_INVENTORY
    }
}
