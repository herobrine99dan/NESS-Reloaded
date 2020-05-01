package com.github.ness.check;

import org.bukkit.entity.Player;
import org.bukkit.event.server.TabCompleteEvent;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class AntiTab {

	public static void Check(TabCompleteEvent e) {
		if (e.getSender() instanceof Player && e.getBuffer().equals("/")) {
			WarnHacks.warnHacks((Player)e.getSender(), "TabComplete", 10, -1.0D, 3, "AntiTab", false);
			e.setCancelled(true);
			return;
		}
	}

}
