package org.mswsplex.MSWS.NESS.api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.mswsplex.MSWS.NESS.NESSPlayer;

public class APIExample {

	@SuppressWarnings("unused")
	@EventHandler
	public void onViolation(PlayerViolationEvent e) {
		String cheat = e.getHack();
		String module = e.getModule();
		int vl = e.getVl();
		e.setCancelled(true);
		NESSPlayer np = e.getNESSPlayer();
		Player p = np.getPlayer();
	}
	
	public static void example() {
		NESSApi api = new NESSApi();
	}
	
}
