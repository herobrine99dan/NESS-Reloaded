package com.github.ness.check.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

public class Strafe extends ListeningCheck<PlayerMoveEvent> {
    
	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos
			.forEvent(PlayerMoveEvent.class);
	private double lastStrafeAngle; // For the Beta NewOldStrafe Check;

	public Strafe(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		lastStrafeAngle = 0;
	}

    @Override
    protected void checkEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        NessPlayer nessPlayer = this.player();
        Vector dir = e.getTo().clone().subtract(e.getFrom()).toVector();
        double angle = Math.toDegrees(Math.atan2(dir.getX(), dir.getZ()));
        double yawDiff = nessPlayer.getMovementValues().getYawDiff();
        lastStrafeAngle = angle;
    }
}
