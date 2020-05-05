package com.github.ness.tinyprotocol.packet.in;

import com.github.ness.tinyprotocol.api.NMSObject;
import com.github.ness.tinyprotocol.api.ProtocolVersion;
import com.github.ness.tinyprotocol.reflection.FieldAccessor;
import com.github.ness.tinyprotocol.packet.types.BaseBlockPosition;
import com.github.ness.tinyprotocol.packet.types.EnumDirection;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInBlockDigPacket extends NMSObject {
    private static final String packet = Client.BLOCK_DIG;

    // Fields
    private static FieldAccessor<Object> fieldBlockPosition = fetchField(packet, Object.class, 0);
    private static FieldAccessor<Object> fieldDirection = fetchField(packet, Object.class, 1);
    private static FieldAccessor<Object> fieldDigType = fetchField(packet, Object.class, 2);

    // Decoded data
    private BaseBlockPosition blockPosition;
    private EnumDirection direction;
    private EnumPlayerDigType action;


    public WrappedInBlockDigPacket(Object packet) {
        super(packet);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        blockPosition = new BaseBlockPosition(fetch(fieldBlockPosition));
        direction = EnumDirection.values()[((Enum) fetch(fieldDirection)).ordinal()];
        action = EnumPlayerDigType.values()[((Enum) fetch(fieldDigType)).ordinal()];
    }

    public enum EnumPlayerDigType {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM
    }
}
