package com.github.ness.check.misc;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import com.github.ness.NessPlayer;
import com.github.ness.check.Check;
import com.github.ness.check.CheckManager;
import com.github.ness.packets.event.FlyingEvent;
import com.github.ness.packets.event.ReceivedPacketEvent;
import com.github.ness.packets.event.UseEntityEvent;

import io.github.retrooper.packetevents.packetwrappers.in.chat.WrappedPacketInChat;

public class AntiUnicode extends Check {

    private static final ThreadLocal<CharsetEncoder> asciiEncoder = ThreadLocal
            .withInitial(() -> StandardCharsets.US_ASCII.newEncoder());

    public AntiUnicode(NessPlayer nessPlayer, CheckManager manager) {
        super(AntiUnicode.class, nessPlayer, manager);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onFlying(FlyingEvent e) {
    }

    @Override
    public void onUseEntity(UseEntityEvent e) {
    }

    @Override
    public void onEveryPacket(ReceivedPacketEvent e) {
        if (e.getPacket().isChat()) {
            WrappedPacketInChat wrapper = new WrappedPacketInChat(e.getPacket().getRawPacket());
            if (!asciiEncoder.get().canEncode(wrapper.getMessage())) {
                flag(e);
            }
        }
    }

}
