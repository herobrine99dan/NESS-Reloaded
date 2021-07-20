package com.github.ness.check.movement.oldmovementchecks;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoFall extends ListeningCheck<PlayerMoveEvent> {
	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public NoFall(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	protected boolean shouldDragDown() {
		return true;
	}

	private void punish(PlayerMoveEvent e, String cheat, String module) {
		NessPlayer nessPlayer = player();
		if (nessPlayer.isTeleported())
			return;
		flagEvent((Cancellable) e);
	}

	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Material below = player.getWorld().getBlockAt(player.getLocation().subtract(0.0D, 1.0D, 0.0D)).getType();
		NessPlayer nessPlayer = player();
		Location from = event.getFrom(), to = event.getTo();
		MovementValues movementValues = nessPlayer.getMovementValues();
		Double hozDist = Double.valueOf(movementValues.getXZDiff());
		Double fallDist = Double.valueOf(player.getFallDistance());
		if (movementValues.getHelper().hasflybypass(nessPlayer) || player.getAllowFlight()
				|| Utility.hasVehicleNear(player) || nessPlayer.isTeleported()
				|| player().getAcquaticUpdateFixes().isRiptiding())
			return;
		Double vertDist = Double.valueOf(movementValues.getyDiff());
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500L) {
			hozDist = Double.valueOf(hozDist.doubleValue() - Math.abs(nessPlayer.getLastVelocity().getX())
					+ Math.abs(nessPlayer.getLastVelocity().getZ()));
			vertDist = Double.valueOf(vertDist.doubleValue() - Math.abs(nessPlayer.getLastVelocity().getY()));
		}
		if (from.getY() - to.getY() > 0.5D && fallDist.doubleValue() <= 0.4D && !below.name().contains("WATER")
				&& !player.getLocation().getBlock().isLiquid()
				&& (hozDist.doubleValue() < 0.2D || !movementValues.isGroundAround())
				&& nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKBROKED) >= 2000L && !nessPlayer.isTeleported()
				&& !below.name().contains("PISTON")
				&& (!player.isInsideVehicle()
						|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))
				&& !player.isFlying() && to.getY() > 0.0D && !movementValues.isNearLiquid()
				&& !movementValues.isNearMaterials("CACTUS,FIRE,SLIME")) {
			boolean gotFire = false;
			if (player.getLastDamageCause() != null && player.getLastDamageCause().getCause() != null
					&& player.getLastDamageCause().getCause().name().toLowerCase().contains("fire"))
				gotFire = true;
			if (!gotFire) {
				punish(event, "NoFall", "(OnMove)");
				double fallDamage = movementValues.getHelper().calcDamageFall(3.0D);
				if (fallDamage > 0.0D)
					player.damage(fallDamage);
			}
		}
	}
}
