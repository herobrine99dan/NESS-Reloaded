package com.github.ness.check;

import org.bukkit.entity.Player;

import com.github.ness.CheckManager;
import com.github.ness.MovementPlayerData;
import com.github.ness.Utility;
import com.github.ness.Violation;
import com.github.ness.events.PacketInEvent;

public class PingSpoof extends AbstractCheck<PacketInEvent> {

	public PingSpoof(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PacketInEvent.class));
	}

	@Override
	void checkEvent(PacketInEvent e) {
		Check(e);
	}

	public void Check(PacketInEvent e) {
		Player sender = e.getPlayer();
		if(sender==null) {
			return;
		}
		MovementPlayerData mp = MovementPlayerData.getInstance(sender);
		mp.pingspooftimer = System.currentTimeMillis();
		double diff = mp.pingspooftimer - mp.oldpingspooftimer;
		if (Utility.getPing(sender) > 300 && (diff > 40) && (diff < 65)) {
			manager.getPlayer(sender).setViolation(new Violation("PingSpoof", "Experimental"));
			Utility.setPing(sender, 100);
		}
		mp.oldpingspooftimer = mp.pingspooftimer;
	}
}
