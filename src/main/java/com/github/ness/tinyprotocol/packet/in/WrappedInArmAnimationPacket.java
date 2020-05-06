package com.github.ness.tinyprotocol.packet.in;

import com.github.ness.tinyprotocol.api.NMSObject;
import lombok.Getter;

@Getter
public class WrappedInArmAnimationPacket extends NMSObject {
    private static final String packet = Client.ARM_ANIMATION;
}
