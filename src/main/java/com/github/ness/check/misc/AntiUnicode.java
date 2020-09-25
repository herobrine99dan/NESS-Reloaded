package com.github.ness.check.misc;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class AntiUnicode extends ListeningCheck<AsyncPlayerChatEvent> {

    private static final ThreadLocal<CharsetEncoder> asciiEncoder = ThreadLocal.withInitial(() -> StandardCharsets.US_ASCII.newEncoder());
	public static final ListeningCheckInfo<AsyncPlayerChatEvent> checkInfo = CheckInfos
			.forEvent(AsyncPlayerChatEvent.class);

	public AntiUnicode(ListeningCheckFactory<?, AsyncPlayerChatEvent> factory, NessPlayer player) {
		super(factory, player);
	}

    @Override
    protected void checkEvent(AsyncPlayerChatEvent e) {
        /**
         * Check if player send Unicode message
         */
        if (!asciiEncoder.get().canEncode(e.getMessage())) {
        	flag();
        	//if(player().setViolation(new Violation("AntiUnicode",""))) e.setCancelled(true);
        }
    }

}
