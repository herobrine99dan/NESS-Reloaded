package com.github.ness.check.movement.oldmovementchecks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckFactory;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.MovementValues;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.MSG;
import com.github.ness.utility.PlayerManager;
import com.github.ness.utility.Utility;

public class NoFall extends AbstractCheck<PlayerMoveEvent> {
	public static final CheckInfo<PlayerMoveEvent> checkInfo = CheckInfo.eventOnly(PlayerMoveEvent.class);

	public NoFall(CheckFactory<?> factory, NessPlayer player) {
		super(factory, player);
	}

	private void punish(PlayerMoveEvent e, String cheat, String module) {
		NessPlayer nessPlayer = this.player();
		if (nessPlayer.isTeleported()) {
			return;
		}
		nessPlayer.setViolation(new Violation(cheat, module), e);
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Material below = player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).getType();
		Material bottom = null;
		NessPlayer nessPlayer = this.player();
		final boolean debugMode = nessPlayer.isDebugMode();
		Location from = event.getFrom(), to = event.getTo();
		MovementValues values = nessPlayer.getMovementValues();
		Double hozDist = values.XZDiff;
		Double fallDist = (double) player.getFallDistance();
		MovementValues movementValues = nessPlayer.getMovementValues();
		if (Utility.hasflybypass(player) || player.getAllowFlight() || Utility.hasVehicleNear(player, 4)
				|| nessPlayer.isTeleported()) {
			return;
		}
		Double vertDist = values.yDiff;
		if (nessPlayer.nanoTimeDifference(PlayerAction.VELOCITY) < 1500) {
			hozDist -= Math.abs(nessPlayer.velocity.getX()) + Math.abs(nessPlayer.velocity.getZ());
			vertDist -= Math.abs(nessPlayer.velocity.getY());
		}
		boolean groundAround = Utility.groundAround(player.getLocation());
		bottom = player.getLocation().getWorld().getBlockAt(player.getLocation().subtract(0, Utility.getDistanceFromGround(player.getLocation()), 0)).getType();

		if (debugMode) {
			MSG.tell(player, "&7X: &e" + player.getLocation().getX() + " &7V: &e" + player.getVelocity().getX());
			MSG.tell(player, "&7Y: &e" + player.getLocation().getY() + " &7V: &e" + player.getVelocity().getY());
			MSG.tell(player, "&7Z: &e" + player.getLocation().getZ() + " &7V: &e" + player.getVelocity().getZ());
			MSG.tell(player, "&7hozDist: &e" + hozDist + " &7vertDist: &e" + vertDist + " &7fallDist: &e" + fallDist);
			MSG.tell(player,
					"&7below: &e" + below.name() + " bottom: " + bottom.name());
			MSG.tell(player,
					"&7groundAround: &e" + MSG.torF(groundAround) + " &7onGround: " + MSG.torF(player.isOnGround()));
		}
		if (to.getY() != from.getY()) {
			if (from.getY() - to.getY() > .3 && fallDist <= .4 && !below.name().toLowerCase().contains("water")
					&& !player.getLocation().getBlock().isLiquid()) {
				if (hozDist < .2 || !groundAround) {
					if (PlayerManager.timeSince("breakTime", player) >= 2000 && !nessPlayer.isTeleported()
							&& !below.name().toLowerCase().contains("piston")) {
						if ((!player.isInsideVehicle()
								|| (player.isInsideVehicle() && player.getVehicle().getType() != EntityType.HORSE))
								&& !player.isFlying() && to.getY() > 0) {
							if (!bottom.name().toLowerCase().contains("slime") && !Utility.hasBlock(player, "water")
									&& !Utility.isInWater(player) && !movementValues.AroundLiquids
									&& !Utility.specificBlockNear(event.getTo(), "fire")
									&& !Utility.getMaterialName(event.getTo()).contains("FIRE") && !Utility
											.getMaterialName(event.getTo().clone().add(0, 0.4, 0)).contains("FIRE")) {
								boolean gotFire = false;
								if (player.getLastDamageCause() != null) {
									if (player.getLastDamageCause().getCause() != null) {
										if (player.getLastDamageCause().getCause().name().toLowerCase()
												.contains("fire")) {
											gotFire = true;
										}
									}
								}
								if (!gotFire) {
									punish(event, "NoFall", "(OnMove)");
									player.damage(
											Math.abs(Utility.calcDamage((3.5 * player.getVelocity().getY()) / -0.71)));
								}
							}
						}
					}
				}
			}
		}
	}
}
