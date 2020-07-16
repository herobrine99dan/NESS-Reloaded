package com.github.ness.packets.checks;

import org.bukkit.entity.Player;

import com.github.ness.MovementPlayerData;
import com.github.ness.api.Violation;
import com.github.ness.check.InventoryHack;
import com.github.ness.utility.Utility;

public class PingSpoof {

	public static boolean Check(Player sender, Object packet) {
		if(sender==null) {
			return false;
		}
			MovementPlayerData mp = MovementPlayerData.getInstance(sender);
			mp.pingspooftimer = System.currentTimeMillis();
			double diff = mp.pingspooftimer - mp.oldpingspooftimer;
			if (Utility.getPing(sender) > 150 && (diff > 40) && (diff < 70)) {
				//sender.teleport(OldMovementChecks.safeLoc.getOrDefault(sender, sender.getLocation()));
				InventoryHack.manageraccess.getPlayer(sender).setViolation(new Violation("PingSpoof",""));
				Utility.setPing(sender, 100);
				return true;
			}
		mp.oldpingspooftimer = mp.pingspooftimer;
		return false;
	}
}
