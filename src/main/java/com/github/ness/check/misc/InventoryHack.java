package com.github.ness.check.misc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.Utility;

public class InventoryHack extends AbstractCheck<InventoryClickEvent> {

	double maxdist;

	public InventoryHack(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(InventoryClickEvent.class));
		this.maxdist = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getDouble("maxdist", 0.1);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkEvent(InventoryClickEvent e) {
		Check(e);
	}

	/**
	 * Check for Impossible InventoryHack or big Distance
	 * 
	 * @param e
	 */
	public void Check(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			if (Utility.hasflybypass(player)) {
				return;
			}
			if (player.isSprinting() || player.isSneaking() || player.isBlocking() || player.isSleeping()
					|| player.isConversing()) {
				manager.getPlayer(player).setViolation(new Violation("InventoryHack", "Impossible"), e);
			} else {
				final Location from = player.getLocation();
				Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
					Location to = player.getLocation();
					double distance = (Math.abs(to.getX() - from.getX())) + (Math.abs(to.getZ() - from.getZ()));
					if (distance > maxdist) {
						manager.getPlayer(player).setViolation(new Violation("InventoryHack", "Dist:" + distance), e);
					}
				}, 2L);
			}
		}
	}
}
