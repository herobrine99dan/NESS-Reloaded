package com.github.ness.check;

import org.bukkit.entity.Player;
import com.github.ness.MovementPlayerData;
import com.github.ness.Utility;

public class PingSpoof {

	public static void Check(Player sender, Object packet) {
		MovementPlayerData mp = MovementPlayerData.getInstance(sender);
		mp.pingspooftimer = System.currentTimeMillis();
		double diff = mp.pingspooftimer - mp.oldpingspooftimer;
		if (Utility.getPing(sender) > 300 && (diff > 40) && (diff < 65)) {

			HACK!
			Utility.setPing(sender, 100);
		}
		mp.oldpingspooftimer = mp.pingspooftimer;
	}
}
