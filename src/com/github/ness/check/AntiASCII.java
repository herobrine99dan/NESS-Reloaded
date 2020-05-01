package com.github.ness.check;

import java.nio.charset.Charset;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ness.CheckManager;

public class AntiASCII extends AbstractCheck<AsyncPlayerChatEvent>  {

	public AntiASCII(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(AsyncPlayerChatEvent.class));
	}

	public static boolean isPureAscii(String v) {
		return Charset.forName("US-ASCII").newEncoder().canEncode(v);
	}

	@Override
	void checkEvent(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (!AntiASCII.isPureAscii(e.getMessage())) {
			//WarnHacks.warnHacks(p, "AntiASCII", 5, -1.0D, 1, "UnicodeCharacter", null);
		}	
	}

}
