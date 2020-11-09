package com.github.ness.check.movement.oldmovementchecks;

import java.time.Duration;

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

public class NoGround extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEventWithAsyncPeriodic(PlayerMoveEvent.class, Duration.ofSeconds(1));
	private int flags;

	public NoGround(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
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
				&& to.getY() % 1.0 == 0 && nessPlayer.milliSecondTimeDifference(PlayerAction.JOIN) >= 1000
				&& !movementValues.AroundStairs && !movementValues.isAroundSlime()) {
			if (!Utility.groundAround(to.clone().add(0, 2, 0))) {
				int failed = flags++;
				if (failed > 3) {
					flagEvent(e);
					//if(player().setViolation(new Violation("NoGround", "(OnMove)"))) e.setCancelled(true);
				}
			}
		}
	}

}
