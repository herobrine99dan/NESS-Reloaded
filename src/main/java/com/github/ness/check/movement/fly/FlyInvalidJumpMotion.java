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
import com.github.ness.data.MovementValuesHelper;
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
		double yDiff = event.getTo().getY() - event.getFrom().getY();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = nessPlayer.getMovementValues();
		if (movementValues.isNearLiquid() || movementValues.hasBlockNearHead()
				|| movementValues.isNearMaterials("ICE", "SLIME", "SNOW", "VINE", "LADDER")
				|| isBlockUpperHeadOnGround(event.getTo()) || isBlockUpperHeadOnGround(event.getFrom())
				|| movementValues.isAroundNonOccludingBlocks() || movementValues.getHelper().hasflybypass(nessPlayer)) {
			return;
		}
		if (yDiff > 0 && !Utility.hasVehicleNear(player)) {
			if (player.getVelocity().getY() == 0.42f
					&& !movementValues.getHelper().isMathematicallyOnGround(event.getTo().getY())
					&& movementValues.getHelper().isMathematicallyOnGround(event.getFrom().getY())) {
				double yResult = Math.abs(yDiff - player.getVelocity().getY());
				if (yResult != 0.0 && nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) > 1700) {
					flagEvent(event, " yResult: " + yResult + "  yDiff: " + yDiff);
				}
			}
		}
	}

	private boolean isBlockUpperHeadOnGround(Location loc) {
		MovementValuesHelper helper = this.player().getMovementValues().getHelper();
		if (helper.isBlockConsideredOnGround(loc.clone().add(0, 1.9, 0)))
			return true; // little optimization
		for (double x = -0.25; x <= 0.25; x += 0.1) {
			for (double z = -0.25; z <= 0.25; z += 0.1) {
				if (helper.isBlockConsideredOnGround(loc.clone().add(x, 1.9, z)))
					return true;
			}
		}
		return false;
	}

}
