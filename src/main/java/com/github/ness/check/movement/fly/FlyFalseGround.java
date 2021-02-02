package com.github.ness.check.movement.fly;

import org.bukkit.Bukkit;
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
		if (movementValues.isAroundLily() || movementValues.isAroundCarpet() || movementValues.isAroundSnow()
				|| this.ness().getMinecraftVersion() > 1152 || this.ness().getMaterialAccess()
						.getMaterial(player.getLocation().clone().add(0, -0.5, 0)).name().contains("SCAFFOLD")) {
			return;
		}
		nessPlayer.sendDevMessage("FlyFalseGround: " + 1);
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500
				&& nessPlayer.getLastVelocity().getY() > 0.35) {
			return;
		}
		nessPlayer.sendDevMessage("FlyFalseGround: " + 2);
		if (movementValues.getHelper().hasflybypass(nessPlayer) || movementValues.isAroundSlime()
				|| Utility.hasLivingEntityNear(player)) {
			return;
		}
		nessPlayer.sendDevMessage("FlyFalseGround: " + 3);
		if (Utility.hasVehicleNear(player) || nessPlayer.getMovementValues().isAroundWeb()) {
			return;
		}
		nessPlayer.sendDevMessage("FlyFalseGround: " + 4);
		if (player.isOnGround() && !movementValues.isGroundAround() && !movementValues.isAroundLadders()) {
			flagEvent(e, " FalseGround");
			// if(player().setViolation(new Violation("Fly", "FalseGround")))
			// e.setCancelled(true);
		} else if (player.isOnGround() && !movementValues.getHelper().isMathematicallyOnGround(e.getTo().getY())
				&& !Bukkit.getVersion().contains("1.8")) {
			flagEvent(e, " FalseGround1");
			// if(player().setViolation(new Violation("Fly", "FalseGround1")))
			// e.setCancelled(true);
		}
	}
}
