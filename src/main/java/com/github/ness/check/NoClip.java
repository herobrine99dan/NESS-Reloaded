package com.github.ness.check;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import com.github.ness.CheckManager;
import com.github.ness.api.Violation;

public class NoClip extends AbstractCheck<PlayerMoveEvent> {
	
	public NoClip(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
       Check(e);//Send the event
	}

	public void Check(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final Location from = event.getFrom();
		final Location to = event.getTo();
		final Double dist = from.distance(to);
		if(player.isFlying() || player.getGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}
		Double hozDist = dist - (to.getY() - from.getY());
		boolean surrounded = true;
		for (int x2 = -2; x2 <= 2; ++x2) {
			for (int y = -2; y <= 3; ++y) {
				for (int z3 = -2; z3 <= 2; ++z3) {
					final Material belowSel2 = player.getWorld()
							.getBlockAt(player.getLocation().add((double) x2, (double) y, (double) z3)).getType();
					if (!belowSel2.isSolid()) {
						surrounded = false;
					}
				}
			}
		}
		if (surrounded && (hozDist > 0.2 || to.getBlockY() < from.getBlockY())) {
			if(manager.getPlayer(player).shouldCancel(event, this.getClass().getSimpleName())) {
				event.setCancelled(true);
			}
			manager.getPlayer(event.getPlayer()).setViolation(new Violation("NoClip",""));
		}

	}

}
