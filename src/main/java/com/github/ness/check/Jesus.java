package com.github.ness.check;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Jesus extends AbstractCheck<PlayerMoveEvent> {

	public Jesus(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
	}

	/**
	 * Check for Physics Check
	 * 
	 * @param event
	 */
	public void Check(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final Location from = event.getFrom().clone();
		final Location to = event.getTo().clone();
		final Block below = player.getWorld().getBlockAt(to.subtract(0.0, 1.0, 0.0));
		boolean lilypad = false;
		final int radius = 2;
		boolean waterAround = false;
		for (int x2 = -1; x2 <= 1; ++x2) {
			for (int z2 = -1; z2 <= 1; ++z2) {
				Material belowSel = player.getWorld()
						.getBlockAt(player.getLocation().add((double) x2, -1.0, (double) z2)).getType();
				belowSel = player.getWorld().getBlockAt(player.getLocation().add((double) x2, -0.01, (double) z2))
						.getType();
				if (belowSel.name().toLowerCase().contains("lily")) {
					lilypad = true;
				} else if (belowSel.name().toLowerCase().contains("carpet")) {
					lilypad = true;
				}
			}
		}
		for (int x2 = -radius; x2 < radius; ++x2) {
			for (int y = -1; y < radius; ++y) {
				for (int z3 = -radius; z3 < radius; ++z3) {
					final Material mat = to.getWorld()
							.getBlockAt(player.getLocation().add((double) x2, (double) y, (double) z3)).getType();
					if (mat.isSolid()) {
						waterAround = true;
					}
				}
			}
		}
		if ((below.isLiquid()) && !player.isFlying() && !waterAround && !lilypad
				&& !player.getWorld().getBlockAt(player.getLocation().add(0.0, 1.0, 0.0)).isLiquid()
				&& (new StringBuilder(String.valueOf(Math.abs(from.getY() - to.getY()))).toString().contains("00000000")
						|| to.getY() == from.getY())) {
			punish(event, player, 3, "Physics");
		}
	}

	private void punish(PlayerMoveEvent e, Player p, int i, String module) {
		if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
			e.setCancelled(true);
		}
		manager.getPlayer(p).setViolation(new Violation("Jesus", module));
	}
}
