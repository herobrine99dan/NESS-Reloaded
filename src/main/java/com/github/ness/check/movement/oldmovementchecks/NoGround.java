package com.github.ness.check.movement.oldmovementchecks;

import java.util.concurrent.TimeUnit;

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

public class NoGround extends AbstractCheck<PlayerMoveEvent> {

	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo.eventWithAsyncPeriodic(PlayerMoveEvent.class,
			1, TimeUnit.SECONDS);
	private int flags;

	public NoGround(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
		flags = 0;
	}
	
	@Override
	protected void checkAsyncPeriodic() {
		flags = 0;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		Player player = e.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = this.player().getMovementValues();
		if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
				|| player().isTeleported()) {
			return;
		}
		if (!(player.isSneaking() && !from.getBlock().getType().name().contains("LADDER")
				&& !to.getBlock().getType().name().contains("LADDER")
				&& !to.clone().add(0, -0.3, 0).getBlock().getType().name().contains("LADDER")) && !player.isOnGround()
				&& to.getY() % 1.0 == 0 && nessPlayer.nanoTimeDifference(PlayerAction.JOIN) >= 1000
				&& !movementValues.AroundStairs && !movementValues.AroundSlime) {
			if (!Utility.groundAround(to.clone().add(0, 2, 0))) {
				int failed = flags++;
				if (failed > 3) {
					if(player().setViolation(new Violation("NoGround", "(OnMove)"))) e.setCancelled(true);
				}
			}
		}
	}

}
