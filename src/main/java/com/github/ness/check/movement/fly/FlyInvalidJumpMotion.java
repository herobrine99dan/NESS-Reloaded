package com.github.ness.check.movement.fly;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class FlyInvalidJumpMotion extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyInvalidJumpMotion(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
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
		if (Utility.getMaterialName(event.getTo().clone().add(0, -0.3, 0)).contains("SLAB")
				|| event.getTo().getBlock().isLiquid() || movementValues.isAroundLiquids() || movementValues.isAroundSnow()
				|| Utility.groundAround(to.clone().add(0, 1.8, 0))
				|| Utility.specificBlockNear(to.clone(), "CHEST")
				|| Utility.specificBlockNear(to.clone(), "LADDER")
				|| Utility.specificBlockNear(to.clone(), "POT")
				|| Utility.specificBlockNear(to.clone(), "BED")
				|| Utility.specificBlockNear(to.clone(), "DETECTOR") || movementValues.isAroundStairs()
				|| Utility.getMaterialName(to.clone().add(0, 1.8, 0)).contains("CHORUS")
				|| Utility.getMaterialName(from.clone().add(0, 1.6, 0)).contains("CHORUS")
				|| movementValues.isAroundIce() || movementValues.isAroundSlime() || Utility.hasflybypass(player)) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1300) {
			yDiff -= Math.abs(nessPlayer.getLastVelocity().getY());
		}
		// !player.getNearbyEntities(4, 4, 4).isEmpty()
		if (yDiff > 0 && !Utility.hasVehicleNear(player, 3)) {
			if (player.getVelocity().getY() == 0.42f && !Utility.isMathematicallyOnGround(event.getTo().getY())
					&& Utility.isMathematicallyOnGround(event.getFrom().getY())) {
				double yResult = Math.abs(yDiff - player.getVelocity().getY());
				if (yResult != 0.0 && nessPlayer.milliSecondTimeDifference(PlayerAction.DAMAGE) > 1700
						&& nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 1700) {
					flagEvent(event, " yResult: " + yResult + "  yDiff: " + yDiff);
	            	//if(player().setViolation(new Violation("Fly", "InvalidJumpMotion yResult: " + yResult + "  yDiff: " + yDiff))) event.setCancelled(true);
				}
			}
		}
	}

}
