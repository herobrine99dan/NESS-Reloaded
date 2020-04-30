package org.mswsplex.MSWS.NESS.checks.chat;

import java.nio.charset.Charset;

import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class AntiASCII {

	public static void Check(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("Phoenix.Bypass"))
			return;
		if (!AntiASCII.isPureAscii(e.getMessage())) {
			WarnHacks.warnHacks(p, "AntiASCII", 5, -1.0D, 1, "UnicodeCharacter", null);
		}
	}

	public static boolean isPureAscii(String v) {
		return Charset.forName("US-ASCII").newEncoder().canEncode(v);
		// or "ISO-8859-1" for ISO Latin 1
		// or StandardCharsets.US_ASCII with JDK1.7+
	}

	public static void Check1(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("Phoenix.Bypass"))
			return;
		if (!AntiASCII.isPureAscii(e.getMessage())) {
			WarnHacks.warnHacks(p, "AntiASCII", 5, -1.0D, 1, "UnicodeCharacter", null);
		}
	}

	public static void Check2(SignChangeEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("Phoenix.Bypass"))
			return;
		for(String s : e.getLines()) {
			if (!AntiASCII.isPureAscii(s)) {
				WarnHacks.warnHacks(p, "AntiASCII", 5, -1.0D, 1, "UnicodeCharacter", null);
			}
		}
	}

}
