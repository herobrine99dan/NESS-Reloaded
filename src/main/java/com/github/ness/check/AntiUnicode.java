package com.github.ness.check;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

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
			ConfigurationSection sec = manager.getNess().getNessConfig().getViolationHandling().getConfigurationSection("cancel");
			if(sec.getBoolean("enabled") && manager.getPlayer(e.getPlayer()).checkViolationCounts.getOrDefault("AntiUnicode", 0)>5) {
				int percentageconfig = sec.getInt("percentage");
				if(Math.random() < percentageconfig / 100.0D) {
					e.setCancelled(true);
				}
			}
		}	
	}

}
