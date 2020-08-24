package com.github.ness.check;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;

public class FastStairs extends AbstractCheck<PlayerMoveEvent> {

	protected static List<String> listbypass = Arrays.asList(new String[] { "0.419", "0.333", "0.248" });

	public FastStairs(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
	}

	public void Check(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location from = e.getFrom().clone().add(0, -0.5, 0);
		Location to = e.getTo().clone().add(0, -0.5, 0);
		NessPlayer np = this.manager.getPlayer(p);
		if (!to.getBlock().getType().name().toLowerCase().contains("stair")
				|| !from.getBlock().getType().name().toLowerCase().contains("stair") || np.isTeleported()) {
			return;
		}
		float distance = (float) np.getMovementValues().XZDiff;
		distance = (float) (distance - (Math.abs(p.getVelocity().getX()) + Math.abs(p.getVelocity().getZ())));
		if (distance > 0.4f && np.lastStairDist > 0.5f) {
			manager.getPlayer(p).setViolation(new Violation("FastStairs", "Distance: " + distance), e);
		}
		np.lastStairDist = distance;
	}

}
