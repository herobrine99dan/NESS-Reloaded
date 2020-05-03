package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.Utility;
import com.github.ness.Violation;

public class InventoryHack extends AbstractCheck<InventoryClickEvent>{
	
	public InventoryHack(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(InventoryClickEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(InventoryClickEvent e) {
       Check(e);
       Check2(e);
	}
	
	public void Check(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			if(Utility.hasflybypass(player)) {
				return;
			}
			if (player.isSprinting() || player.isSneaking() || player.isBlocking() || player.isSleeping()
					|| player.isConversing()) {
				manager.getPlayer(player).setViolation(new Violation("InventoryHack"));
			} else {
				final Location from = player.getLocation();
				Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
					Location to = player.getLocation();
					double distance = to.distanceSquared(from) - Math.abs(from.getY() - to.getBlockY());
					if (distance > 0.05) {
						manager.getPlayer(player).setViolation(new Violation("InventoryHack"));							// MSG.tell(player, "Distance " + distance);
					}
				}, 2L);
			}
		}
	}
	
	public void Check2(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			NessPlayer p = manager.getPlayer(player);
			p.setClicks(p.getClicks()+1);
            if(p.getClicks()>4) {
				p.setViolation(new Violation("FastClick"));							// MSG.tell(player, "Distance " + distance);
           	 e.setCancelled(true);
            }
		}
	}
	
}
