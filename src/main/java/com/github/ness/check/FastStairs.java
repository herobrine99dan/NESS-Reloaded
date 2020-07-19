package com.github.ness.check;

import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utilities;
import com.github.ness.utility.Utility;

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
		Location from = e.getFrom();
		Location to = e.getTo();
		String blockName = Utilities.getPlayerUnderBlock(p).getType().name();
		if (!blockName.contains("STAIR") || p.getFallDistance() != 0.0F
				|| (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
				|| this.manager.getPlayer(p).isTeleported())
			return;
		double distance = Utility.around(Math.abs(Utility.getMaxSpeed(e.getFrom(), e.getTo())), 6);
		double ydist = Utility.around(to.getY() - from.getY(), 6);
		if (distance > 0.4D && !listbypass.contains(Double.toString(ydist))) {
			manager.getPlayer(p).setViolation(new Violation("FastStairs", "Distance: " + distance));
			if(manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
				e.setCancelled(true);
			}
		}
	}

}
