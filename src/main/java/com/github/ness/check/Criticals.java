package com.github.ness.check;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.CheckManager;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Criticals extends AbstractCheck<EntityDamageByEntityEvent> {

	public Criticals(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
		//newCheck(e);
	}

	public void Check(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (!player.isOnGround() && player.getFallDistance() > 0 && !Utility.hasflybypass(player)
					&& !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
					&& !player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid()) {
				if (player.getLocation().getY() % 1.0D == 0.0D
						&& player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
					if (manager.getPlayer(player).shouldCancel(event, this.getClass().getSimpleName())) {
						event.setCancelled(true);
					}
					manager.getPlayer(player).setViolation(new Violation("Criticals", ""));
				}
			}
		}
	}

}
