package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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

public class NoFall extends ListeningCheck<PlayerMoveEvent> {
	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	public NoFall(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
	}

	@Override
	protected boolean shouldDragDown() {
		return true;
	}

	private void punish(PlayerMoveEvent e, String cheat, String module) {
		NessPlayer nessPlayer = this.player();
		if (nessPlayer.isTeleported()) {
			return;
		}
		flagEvent(e);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Material below = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).getType();
		NessPlayer nessPlayer = this.player();
		Location from = event.getFrom(), to = event.getTo();
		MovementValues movementValues = nessPlayer.getMovementValues();
		Double hozDist = movementValues.getXZDiff();
		Double fallDist = (double) player.getFallDistance();
		if (movementValues.getHelper().hasflybypass(nessPlayer) || player.getAllowFlight()
				|| Utility.hasVehicleNear(player) || nessPlayer.isTeleported()) {
			return;
		}
		Double vertDist = movementValues.getyDiff();
		if (nessPlayer.milliSecondTimeDifference(PlayerAction.VELOCITY) < 1500) {
			hozDist -= Math.abs(nessPlayer.getLastVelocity().getX()) + Math.abs(nessPlayer.getLastVelocity().getZ());
			vertDist -= Math.abs(nessPlayer.getLastVelocity().getY());
		}
		if (from.getY() - to.getY() > .3 && fallDist <= .4 && !below.name().contains("WATER")
				&& !player.getLocation().getBlock().isLiquid()) {
			if (hozDist < .2 || !movementValues.isGroundAround()) {
				if (nessPlayer.milliSecondTimeDifference(PlayerAction.BLOCKBROKED) >= 2000 && !nessPlayer.isTeleported()
						&& !below.name().contains("PISTON")) {
					if ((!player.isInsideVehicle()
							|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))
							&& !player.isFlying() && to.getY() > 0) {
						if (!movementValues.isAroundSlime() && !movementValues.isAroundLiquids()
								&& !movementValues.isAroundLiquids() && !movementValues.isAroundLiquids()
								&& !movementValues.isAroundFire() && !movementValues.isAroundFire() && !movementValues.isAroundCactus()) {
							boolean gotFire = false;
							if (player.getLastDamageCause() != null) {
								if (player.getLastDamageCause().getCause() != null) {
									if (player.getLastDamageCause().getCause().name().toLowerCase().contains("fire")) {
										gotFire = true;
									}
								}
							}
							if (!gotFire) {
								punish(event, "NoFall", "(OnMove)");
								double fallDamage = movementValues.getHelper().calcDamageFall(3);
								if (fallDamage > 0) {
									player.damage(fallDamage);
								}
							}
						}
					}
				}
			}
		}
	}
}
