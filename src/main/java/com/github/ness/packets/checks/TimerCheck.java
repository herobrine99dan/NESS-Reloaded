package com.github.ness.packets.checks;

import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;

public class TimerCheck {
	
	public static void Check(Player p, Object packet) {
		if(!packet.toString().toLowerCase().contains("flying")) {
			return;
		}
		NessPlayer np = NESSAnticheat.getInstance().getCheckManager().getPlayer(p);
		final long result = System.currentTimeMillis() - np.lastFlyingPacket;
		np.lastFlyingPacket = System.currentTimeMillis();
		if (result < 23) {
			if(result > 3) {
				p.sendMessage("Result: " + result);
			}
		}
	}

}
