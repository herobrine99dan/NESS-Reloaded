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
	void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location to = event.getTo();
		NessPlayer np = this.manager.getPlayer(p);
		double xzDiff = np.getMovementValues().XZDiff;
		if(Utility.isMathematicallyOnGround(to.getY()) || xzDiff == 0.0) {
			return;
		}
		double result = Math.abs(xzDiff - np.lastStrafeDist);
		if(result == 0.0 && !Utility.blockAdjacentIsLiquid(to) && !Utility.nextToWall(p) && !p.isFlying() && !p.getAllowFlight()) {
			manager.getPlayer(p).setViolation(new Violation("Strafe", "EqualDist"));
			if (manager.getPlayer(event.getPlayer()).shouldCancel(event, this.getClass().getSimpleName())) {
				event.setCancelled(true);
			}
		}
		np.lastStrafeDist = xzDiff;
	}
}
