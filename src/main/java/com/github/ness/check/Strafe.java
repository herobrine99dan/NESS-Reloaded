package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Strafe extends AbstractCheck<PlayerMoveEvent> {

	public Strafe(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location to = e.getTo();
		Location from = e.getFrom();
		NessPlayer np = this.manager.getPlayer(p);
		float xDiff = (float) np.getMovementValues().xDiff;
		float zDiff = (float) np.getMovementValues().zDiff;
		if (this.manager.getPlayer(p).isTeleported() || Utility.hasVehicleNear(p, 3) || Utility.hasEntityNear(p, 3)) {
			return;
		}
		if (!Utility.isMathematicallyOnGround(to.getY()) && !Utility.isMathematicallyOnGround(from.getY()) && !Utility.hasflybypass(e.getPlayer())) {
			float resultX = (float) Math.abs(xDiff - p.getVelocity().getX());
			float resultZ = (float) Math.abs(zDiff - p.getVelocity().getZ());
			if(resultX > 0.28 || resultZ > 0.28) {
				manager.getPlayer(p).setViolation(new Violation("Strafe", "HighDistance"));
				if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		}
	}
}
