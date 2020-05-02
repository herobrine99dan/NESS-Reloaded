package com.github.ness.check;

import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.PlayerManager;
import com.github.ness.MovementPlayerData;

public class PingSpoof {

	public static void Check(Player sender, Object packet) {
		MovementPlayerData mp = MovementPlayerData.getInstance(sender);
		mp.pingspooftimer = System.currentTimeMillis();
		double diff = mp.pingspooftimer - mp.oldpingspooftimer;
		if (PlayerManager.getPing(sender) > 300 && (diff > 40) && (diff < 65)) {
			if (NESS.main.devMode) {
				sender.sendMessage("PingSpoof: difference " + diff + " Ping: " + PlayerManager.getPing(sender));
			}//To rember this
			HACK!
			PlayerManager.setPing(sender, 100);
		}
		mp.oldpingspooftimer = mp.pingspooftimer;
	}
}
