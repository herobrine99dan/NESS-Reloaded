package org.mswsplex.MSWS.NESS.checks.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.MSWS.NESS.WarnHacks;

public class Headless {

	public static void Check(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        float pitch = p.getLocation().getPitch();
        if(pitch < -90 || pitch > 90)
        {
            WarnHacks.warnHacks(p, "Headless", 5, -1.0D, 3, "InvalidPitch", false);
            return;
        }
	}

}
