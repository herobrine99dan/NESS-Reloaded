package com.github.ness.check;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import com.github.ness.CheckManager;
import com.github.ness.Violation;
import com.github.ness.utility.Utilities;
import com.github.ness.utility.Utility;

public class Jesus extends AbstractCheck<PlayerMoveEvent> {

	public Jesus(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
		Check1(e);
		Check2(e);
	}

	public List<Player> placedBlockOnWater = new ArrayList<>();
    /**
     * Check for Physics Check
     * @param event
     */
	public void Check(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final Material below = player.getWorld().getBlockAt(player.getLocation().subtract(0.0, 1.0, 0.0)).getType();
		final Location from = event.getFrom();
		final Location to = event.getTo();
		boolean lilypad = false;
		final int radius = 2;
		boolean waterAround = false;
		for (int x2 = -1; x2 <= 1; ++x2) {
			for (int z2 = -1; z2 <= 1; ++z2) {
				Material belowSel = player.getWorld()
						.getBlockAt(player.getLocation().add((double) x2, -1.0, (double) z2)).getType();
				belowSel = player.getWorld().getBlockAt(player.getLocation().add((double) x2, -0.01, (double) z2))
						.getType();
				if (belowSel == Material.WATER_LILY) {
					lilypad = true;
				} else if (belowSel.equals(Material.CARPET)) {
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
		if ((below == Material.WATER || below == Material.STATIONARY_WATER || below == Material.LAVA
				|| below == Material.STATIONARY_LAVA) && !player.isFlying() && !waterAround && !lilypad
				&& !player.getWorld().getBlockAt(player.getLocation().add(0.0, 1.0, 0.0)).isLiquid()
				&& (new StringBuilder(String.valueOf(Math.abs(from.getY() - to.getY()))).toString().contains("00000000")
						|| to.getY() == from.getY())) {
			punish(player, 3, "Physics");
		}
	} 
    /**
     * Another Physics Check
     * @param event
     */
	protected void Check1(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!event.isCancelled()
				&& (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ())
				&& !p.getAllowFlight() && !Utilities.isOnLilyPad(p)
				&& !p.getLocation().clone().add(0.0D, 0.4D, 0.0D).getBlock().getType().isSolid()
				&& !placedBlockOnWater.remove(p)) {
			if (Utilities.cantStandAtWater(p.getWorld().getBlockAt(p.getLocation()))
					&& Utilities.isHoveringOverWater(p.getLocation()) && !Utilities.isFullyInWater(p.getLocation())) {
				punish(p, 2, "Vanilla");
			}
		}
	}

	private void punish(Player p, int i, String module) {
		manager.getPlayer(p).setViolation(new Violation("Jesus",module));
	}
    /**
     * Big Distance
     * @param e
     */
	public void Check2(PlayerMoveEvent e) {
		double fromy = e.getFrom().getY();
		double toy = e.getTo().getY();
		Player player = e.getPlayer();
		if (Utility.hasflybypass(player)) {
			return;
		}
		double resulty = Math.abs(fromy - toy);
		double distance = e.getTo().distance(e.getFrom()) - resulty;
		Block block = player.getLocation().getBlock();
		Location loc = player.getLocation();
		loc.setY(loc.getY() - 1);
		Location underloc = player.getLocation();
		underloc.setY(underloc.getY() + 1);
		if ((player.getVehicle() == null) && (!player.isFlying())) {
			float distanceFell = player.getFallDistance();
			if (block.isLiquid() && loc.getBlock().isLiquid() && distanceFell < 1 && !underloc.getBlock().isLiquid()) {
				if (distance > 0.11863034217827088) {
					// MSG.tell((CommandSender)player, "&7dist: &e" + distance);
					punish(e.getPlayer(), 2, "WaterSpeed");
				}
			}
		}
	}

}
