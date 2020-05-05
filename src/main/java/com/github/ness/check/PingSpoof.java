package com.github.ness.check;

import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.github.ness.CheckManager;
import com.github.ness.MovementPlayerData;
import com.github.ness.Violation;
import com.github.ness.events.PacketInEvent;
import com.github.ness.utility.Utility;

public class PingSpoof {

	public void Check(Player sender, PacketContainer packet) {
		if(sender==null) {
			return;
		}
		MovementPlayerData mp = MovementPlayerData.getInstance(sender);
		mp.pingspooftimer = System.currentTimeMillis();
		double diff = mp.pingspooftimer - mp.oldpingspooftimer;
		if (Utility.getPing(sender) > 300 && (diff > 40) && (diff < 65)) {
			InventoryHack.manageraccess.getPlayer(sender).setViolation(new Violation("PingSpoof"));
			Utility.setPing(sender, 100);
		}
		mp.oldpingspooftimer = mp.pingspooftimer;
	}
}
