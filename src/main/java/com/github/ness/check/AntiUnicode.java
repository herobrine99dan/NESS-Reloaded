package com.github.ness.check;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ness.CheckManager;
import com.github.ness.Violation;

public class AntiUnicode extends AbstractCheck<AsyncPlayerChatEvent>  {

	private static final ThreadLocal<CharsetEncoder> asciiEncoder = ThreadLocal.withInitial(() -> StandardCharsets.US_ASCII.newEncoder());
	
	public AntiUnicode(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(AsyncPlayerChatEvent.class));
	}

	@Override
	void checkEvent(AsyncPlayerChatEvent e) {
		/**
		 * Check if player send Unicode message
		 */
		if (!asciiEncoder.get().canEncode(e.getMessage())) {
			manager.getPlayer(e.getPlayer()).setViolation(new Violation("AntiUnicode", e.getMessage()));
		}	
	}

}
