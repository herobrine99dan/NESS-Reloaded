package com.github.ness.check.movement.fly;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.ImmutableLoc;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.ReflectionUtility;
import com.github.ness.utility.Utility;

public class FlyFalseGround extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public FlyFalseGround(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		NessPlayer nessPlayer = this.player();
		MovementValues movementValues = nessPlayer.getMovementValues();
		ness();
		if (Bukkit.getVersion().contains("1.8") || movementValues.isAroundLily() || movementValues.isAroundCarpet()
				|| movementValues.isAroundSnow() || NESSAnticheat.getMinecraftVersion() > 1152
				|| ReflectionUtility.getBlockName(player, ImmutableLoc.of(player.getLocation().clone().add(0, -0.5, 0)))
						.contains("scaffolding")) {
			return;
		}
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500
				&& nessPlayer.getLastVelocity().getY() > 0.35) {
			return;
		}
		if (!nessPlayer.isTeleported() && player.getNearbyEntities(2, 2, 2).isEmpty() && !Utility.hasflybypass(player)
				&& player.isOnline() && !movementValues.isAroundSlime() && !player.isInsideVehicle()
				&& !player().getMovementValues().isAroundWeb()) {
			if (player.isOnGround() && !Utility.groundAround(e.getTo()) && !movementValues.isAroundLadders()) {
				flagEvent(e, " FalseGround");
				// if(player().setViolation(new Violation("Fly", "FalseGround")))
				// e.setCancelled(true);
			} else if (player.isOnGround() && !Utility.isMathematicallyOnGround(e.getTo().getY())) {
				flagEvent(e, " FalseGround1");
				// if(player().setViolation(new Violation("Fly", "FalseGround1")))
				// e.setCancelled(true);
			}
		}
	}
}
