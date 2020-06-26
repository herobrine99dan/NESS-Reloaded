package com.github.ness.packets;

import java.io.ObjectInputFilter.Config;

import org.bukkit.entity.Player;

public class BadPackets {

	/**
	 * @author koloslolya
	 */
	public static boolean check(Player sender, String packetName)  {
		PlayerPacketsData pd = PlayerPacketsData.getInstance(sender);
            if (packetName.toLowerCase().contains("flying")) {
                long timeX = time();
                long lastTime = pd.lastTime != 0 ? (long) pd.lastTime : timeX - 50;
                pd.lastTime = timeX;
                pd.balance += 50;
                pd.balance -= (timeX - lastTime);
                // System.out.println(pd.balance + " " + (timeX - lastTime) + " " + lastTime);
                if (pd.balance > 10) {
                    return new CheckResult(String.format("balance: %s",
                            pd.balance),
                            false);
                }
            } else if (packetName.toLowerCase().contains("position")) {
                pd.balance += 50;
            }
            
        return new CheckResult("0",true);
	}
}
