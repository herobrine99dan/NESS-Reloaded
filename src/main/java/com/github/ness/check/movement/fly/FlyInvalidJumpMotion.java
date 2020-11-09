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
				|| event.getTo().getBlock().isLiquid() || movementValues.AroundLiquids || movementValues.isAroundSnow()
				|| Utility.groundAround(to.clone().add(0, 1.8, 0))
				|| Utility.specificBlockNear(to.clone(), "chest")
				|| Utility.specificBlockNear(to.clone(), "ladder")
				|| Utility.specificBlockNear(to.clone(), "pot")
				|| Utility.specificBlockNear(to.clone(), "bed")
				|| Utility.specificBlockNear(to.clone(), "detector") || movementValues.AroundStairs
				|| Utility.getMaterialName(to.clone().add(0, 1.8, 0)).contains("CHORUS")
				|| Utility.getMaterialName(from.clone().add(0, 1.6, 0)).contains("CHORUS")
				|| Utility.getMaterialName(to).contains("LADDER")
				|| Utility.getMaterialName(to).contains("VINE")
				|| Utility.getMaterialName(to).contains("SEA")
				|| Utility.getMaterialName(from).contains("SEA")
				|| Utility.getMaterialName(to.clone().add(0, 0.3, 0)).contains("SEA")
				|| Utility.getMaterialName(to.clone().add(0, -0.2, 0)).contains("SEA")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).contains("LADDER")
				|| movementValues.AroundIce || movementValues.isAroundSlime() || Utility.hasflybypass(player)) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1300) {
			yDiff -= Math.abs(nessPlayer.getLastVelocity().getY());
		}
		// !player.getNearbyEntities(4, 4, 4).isEmpty()
		if (yDiff > 0 && !player.isInsideVehicle()) {
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
