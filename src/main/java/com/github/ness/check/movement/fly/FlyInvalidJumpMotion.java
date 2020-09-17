package com.github.ness.check.movement.fly;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FlyInvalidJumpMotion extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo.eventOnly(PlayerMoveEvent.class);

	public FlyInvalidJumpMotion(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		double yDiff = event.getTo().getY() - event.getFrom().getY();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = nessPlayer.getMovementValues();
		if (Utility.getMaterialName(event.getTo().clone().add(0, -0.3, 0)).contains("slab")
				|| event.getTo().getBlock().isLiquid() || movementValues.AroundLiquids || movementValues.AroundSnow
				|| Utility.groundAround(to.clone().add(0, 1.8, 0))
				|| Utility.specificBlockNear(to.clone(), "chest")
				|| Utility.specificBlockNear(to.clone(), "ladder")
				|| Utility.specificBlockNear(to.clone(), "pot")
				|| Utility.specificBlockNear(to.clone(), "bed")
				|| Utility.specificBlockNear(to.clone(), "detector") || movementValues.AroundStairs
				|| Utility.getMaterialName(to.clone().add(0, 1.8, 0)).contains("chorus")
				|| Utility.getMaterialName(from.clone().add(0, 1.6, 0)).contains("chorus")
				|| Utility.getMaterialName(to).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(to).toLowerCase().contains("vine")
				|| Utility.getMaterialName(to).toLowerCase().contains("sea")
				|| Utility.getMaterialName(from).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to.clone().add(0, 0.3, 0)).toLowerCase().contains("sea")
				|| Utility.getMaterialName(to.clone().add(0, -0.2, 0)).toLowerCase().contains("sea")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| movementValues.AroundIce || movementValues.AroundSlime || Utility.hasflybypass(player)) {
			return;
		}
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
