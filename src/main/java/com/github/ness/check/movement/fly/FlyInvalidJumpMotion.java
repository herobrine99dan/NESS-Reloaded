package com.github.ness.check.movement.fly;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FlyInvalidJumpMotion extends AbstractCheck<PlayerMoveEvent> {

	public FlyInvalidJumpMotion(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		double yDiff = event.getTo().getY() - event.getFrom().getY();
		if (Utility.getMaterialName(event.getTo().clone().add(0, -0.3, 0)).contains("slab")
				|| event.getTo().getBlock().isLiquid()
				|| event.getTo().clone().add(0, 1.9, 0).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0.3, 1.9, 0).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0, 1.9, 0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(-0.3, 1.9, 0).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0, 1.9, -0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0.3, 1.9, 0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(-0.3, 1.9, -0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(0.3, 1.9, -0.3).getBlock().getType().isSolid()
				|| event.getTo().clone().add(-0.3, 1.9, 0.3).getBlock().getType().isSolid()
				|| Utility.specificBlockNear(event.getTo().clone(), "liquid") || Utility.hasflybypass(player)
				|| Utility.specificBlockNear(player.getLocation().clone(), "snow")
				|| Utility.groundAround(to.clone().add(0, 1.8, 0))
				|| Utility.specificBlockNear(player.getLocation().clone(), "chest")
				|| Utility.specificBlockNear(player.getLocation().clone(), "ladder")
				|| Utility.specificBlockNear(player.getLocation().clone(), "pot")
				|| Utility.specificBlockNear(player.getLocation().clone(), "bed")
				|| Utility.specificBlockNear(player.getLocation().clone(), "detector")
				|| Utility.specificBlockNear(player.getLocation().clone(), "stair")
				|| Utility.getMaterialName(to.clone().add(0, -1, 0)).contains("chest")
				|| Utility.getMaterialName(from.clone().add(0, -1, 0)).contains("chest")
				|| Utility.getMaterialName(to.clone().add(0, 1.8, 0)).contains("chorus")
				|| Utility.getMaterialName(from.clone().add(0, 1.6, 0)).contains("chorus")
				|| Utility.getMaterialName(to).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(to).toLowerCase().contains("vine")
				|| Utility.getMaterialName(from).toLowerCase().contains("vine")
				|| Utility.getMaterialName(to).toLowerCase().contains("sea")
				|| Utility.getMaterialName(from).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to.clone().add(0, 0.3, 0)).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to.clone().add(0, -0.2, 0)).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to).toLowerCase().contains("pot")
				|| Utility.getMaterialName(from).toLowerCase().contains("pot")
				|| Utility.getMaterialName(to.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(to.clone().add(0, 0.5, 0)).toLowerCase().contains("bed")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("bed")
				|| Utility.getMaterialName(to.clone().add(0, -1, 0)).contains("detector")
				|| Utility.getMaterialName(to.clone().add(0, -0.5, 0)).contains("slime")
				|| Utility.getMaterialName(from.clone().add(0, -1, 0)).contains("detector")
				|| Utility.hasBlock(player, "slime") || Utility.specificBlockNear(to.clone(), "ice")
				|| Utility.hasflybypass(player)) {
			return;
		}
		NessPlayer nessPlayer = this.manager.getPlayer(player);
		if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1300) {
			yDiff -= Math.abs(nessPlayer.velocity.getY());
		}
		// !player.getNearbyEntities(4, 4, 4).isEmpty()
		if (yDiff > 0 && !player.isInsideVehicle()) {
			if (player.getVelocity().getY() == 0.42f && !Utility.isMathematicallyOnGround(event.getTo().getY())
					&& Utility.isMathematicallyOnGround(event.getFrom().getY())) {
				double yResult = Math.abs(yDiff - player.getVelocity().getY());
				if (yResult != 0.0 && nessPlayer.nanoTimeDifference(PlayerAction.DAMAGE) > 1700
						&& nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) > 1700) {
					nessPlayer.setViolation(
							new Violation("Fly", "InvalidJumpMotion yResult: " + yResult + "  yDiff: " + yDiff), event);
				}
			}
		}
	}

}
