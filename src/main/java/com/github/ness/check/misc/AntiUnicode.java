package com.github.ness.check.misc;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;

public class AntiUnicode extends AbstractCheck<AsyncPlayerChatEvent> {

    private static final ThreadLocal<CharsetEncoder> asciiEncoder = ThreadLocal.withInitial(() -> StandardCharsets.US_ASCII.newEncoder());
	public static final CheckInfo<AsyncPlayerChatEvent> checkInfo = CheckInfo
			.eventOnly(AsyncPlayerChatEvent.class);

	public AntiUnicode(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(AsyncPlayerChatEvent e) {
        /**
         * Check if player send Unicode message
         */
        if (!asciiEncoder.get().canEncode(e.getMessage())) {
        	if(player().setViolation(new Violation("AntiUnicode",""))) e.setCancelled(true);
        }
    }

}
