package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class InventoryHack extends AbstractCheck<InventoryClickEvent> {

	public static CheckManager manageraccess;

	public InventoryHack(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(InventoryClickEvent.class));
		manageraccess = manager;
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(InventoryClickEvent e) {
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
				if (manager.getPlayer(player).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
				manager.getPlayer(player).setViolation(new Violation("InventoryHack", "Impossible"));
			} else {
				final Location from = player.getLocation();
				Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
					Location to = player.getLocation();
					double distance = to.distanceSquared(from);
					if (distance > 0.1) {
						if (manager.getPlayer(player).shouldCancel(e, this.getClass().getSimpleName())) {
							e.setCancelled(true);
						}
						manager.getPlayer(player).setViolation(new Violation("InventoryHack", "Dist:" + distance)); // MSG.tell(player,
																													// "Distance
																													// "
																													// +
																													// distance);
					}
				}, 2L);
			}
		}
	}
}
