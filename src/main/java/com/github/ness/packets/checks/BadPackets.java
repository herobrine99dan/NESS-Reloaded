package com.github.ness.packets.checks;

import org.bukkit.entity.Player;

import com.github.ness.api.Violation;
import com.github.ness.check.InventoryHack;
import com.github.ness.packets.PlayerPacketsData;

public class BadPackets {

	/**
	 * @author koloslolya
	 * @return
	 */
	public static boolean Check(Player sender, String packetName) {
		PlayerPacketsData pd = PlayerPacketsData.getInstance(sender);
		if (packetName.toLowerCase().contains("flying")) {
			long timeX = System.nanoTime() / 1000000;
			long lastTime = pd.lastTime != 0 ? (long) pd.lastTime : timeX - 50;
			pd.lastTime = timeX;
			pd.balance += 50;
			pd.balance -= (timeX - lastTime);
			// System.out.println(pd.balance + " " + (timeX - lastTime) + " " + lastTime);
			if (pd.balance > 10) {
				InventoryHack.manageraccess.getPlayer(sender)
						.setViolation(new Violation("BadPackets", pd.balance + ""));
				pd.balance = -100;
				return true;
			}
		} else if (packetName.toLowerCase().contains("position")) {
			pd.balance += 50;
		}
		return false;
	}

}
