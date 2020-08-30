package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Speed extends AbstractCheck<PlayerMoveEvent> {

	public Speed(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerMoveEvent.class, 1, TimeUnit.SECONDS));
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check1(e);
	}

	@Override
	void checkAsyncPeriodic(NessPlayer player) {
	}

	private void punish(PlayerMoveEvent e, String module) {
		Player p = e.getPlayer();
		if (Utility.hasflybypass(p) || manager.getPlayer(e.getPlayer()).isTeleported()) {
			return;
		}
		manager.getPlayer(p).setViolation(new Violation("Speed", module), e);
	}

	public void Check1(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		float dist = (float) np.getMovementValues().XZDiff; //Our XZ Distance
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		int speedLevel = Utility.getPotionEffectLevel(p, PotionEffectType.SPEED); //We handle the speed potion
		if (speedLevel > 2 || p.getGameMode() == GameMode.SPECTATOR || p.isInsideVehicle() || np.isTeleported()
				|| Utility.hasVehicleNear(p, 3) || Utility.hasflybypass(p)) {
			return;
		}
		//TODO Handle Slowness Potion (Slowness decreases walking speed by 15% × level)
		final double f = to.getYaw() * 0.017453292F;
		double walkSpeed = p.isSprinting() && !p.hasPotionEffect(PotionEffectType.BLINDNESS) ? (0.3 * p.getWalkSpeed()) / 0.2 : p.getWalkSpeed();
		final double resultX = Math.abs((Math.sin(f) * walkSpeed)); //We calculate the correct speed Distance for X and Z coords
		final double resultZ = Math.abs((Math.cos(f) * walkSpeed));
		double maxDist = resultX + resultZ;
		final boolean isInWater = to.getBlock().isLiquid() && to.clone().add(0, -0.1, 0).getBlock().isLiquid();
		final double xzVelocity = (Math.abs(p.getVelocity().getX()) + Math.abs(p.getVelocity().getZ())) * 1.18; //We add the player velocity to have compatibility with other plugins and to add speed in air
		final double yVelocity = Math.abs(p.getVelocity().getY()) * 0.26; //For safety we add also the yVelocity (0.42 = 0.105)
		maxDist += xzVelocity;
		maxDist += yVelocity;
		if (to.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| from.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| Utility.specificBlockNear(from.clone().add(0, -0.4, 0), "ice")) {
			maxDist *= 1.3;
		}
		//We handle Sneaking
		if (p.isSneaking()) {
			maxDist = adaptWalkSpeed(0.155,p);
			maxDist += xzVelocity;
			maxDist += yVelocity;
		}
		if (isInWater) {
			maxDist = getMaxWaterSpeed(walkSpeed);
			maxDist += yVelocity;
			if (p.isSprinting()) {
				maxDist += 0.1f;
			}
		}

		if (Utility.getMaterialName(to).contains("web") || Utility.getMaterialName(from).contains("web")
				|| Utility.getMaterialName(to.clone().add(0, 0.2, 0)).contains("web")
				|| Utility.getMaterialName(from.clone().add(0, 0.2, 0)).contains("web")) {
			maxDist = adaptWalkSpeed(0.172,p);
			if (p.isSprinting()) {
				maxDist *= 1.21f;
			}
			maxDist += xzVelocity;
			maxDist += yVelocity;
		}
		if (Utility.getMaterialName(to).contains("stairs") || Utility.getMaterialName(from).contains("stairs")
				|| Utility.getMaterialName(to.clone().add(0, 0.3, 0)).contains("stairs")
				|| Utility.getMaterialName(to.clone().add(0, -0.3, 0)).contains("stairs")) {
			maxDist = walkSpeed * 1.58;
			maxDist += xzVelocity;
			maxDist += yVelocity;
		}
		if (p.getAllowFlight()) {
			maxDist += 1.3;
		}
		if (speedLevel > 0) {
			dist -= (dist / 100.0) * (speedLevel * 20.0);
		}
		float pingresult = Utility.getPing(p) / 100;
		float toAdd = pingresult / 7;
		maxDist += toAdd;
		double result = dist - maxDist;
		// p.sendMessage("maxDist: " + maxDist + " Dist: " + dist);
		if (result > 0.1 && !np.isTeleported()) {
			this.punish(event, "MaxDistance: " + dist + " Max: " + maxDist);
		}
	}
	
	private double adaptWalkSpeed(double n, Player p) {
		return (n * p.getWalkSpeed()) / 0.2;
	}

	private double getMaxWaterSpeed(double n) {
		if (NESSAnticheat.getInstance().getMinecraftVersion() > 1122) {
			return n * 0.7;
		}
		return n * 0.6;
	}
}
