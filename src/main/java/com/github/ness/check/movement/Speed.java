package com.github.ness.check.movement;

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
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.data.PlayerAction;
import com.github.ness.utility.Utility;

public class Speed extends AbstractCheck<PlayerMoveEvent> {

	public Speed(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent e) {
		Check(e);
	}

	private void punish(PlayerMoveEvent e, String module) {
		Player p = e.getPlayer();
		if (Utility.hasflybypass(p) || manager.getPlayer(e.getPlayer()).isTeleported()) {
			return;
		}
		manager.getPlayer(p).setViolation(new Violation("Speed", module), e);
	}

	public void Check(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		float dist = (float) np.getMovementValues().XZDiff; // Our XZ Distance
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		int speedLevel = Utility.getPotionEffectLevel(p, PotionEffectType.SPEED);
		if (speedLevel > 2 || p.getGameMode() == GameMode.SPECTATOR || p.isInsideVehicle() || np.isTeleported()
				|| Utility.hasVehicleNear(p, 3) || Utility.hasflybypass(p)) {
			return;
		}
		// TODO Handle Slowness Potion (Slowness decreases walking speed by 15% × level)
		double walkSpeed = p.isSprinting() && !p.hasPotionEffect(PotionEffectType.BLINDNESS)
				? (0.3 * p.getWalkSpeed()) / 0.2
				: p.getWalkSpeed();
		//We adapt the Distance to the yaw
		final double f = to.getYaw() * 0.017453292F;
		final double resultX = Math.abs((Math.sin(f) * walkSpeed));
		final double resultZ = Math.abs((Math.cos(f) * walkSpeed));
		double maxDist = resultX + resultZ;
		//We remove the speed distance from the player distance
		if (speedLevel > 0) {
			dist -= (dist / 100.0) * (speedLevel * 20.0);
		}
		//We add Player Velocity to prevent false flags with KnockBack or custom Velocity
		final double xzVelocity = (Math.abs(p.getVelocity().getX()) + Math.abs(p.getVelocity().getZ())) * 1.2;
		final double yVelocity = Math.abs(p.getVelocity().getY()) * 0.26;
		maxDist += xzVelocity;
		maxDist += yVelocity;
		//When you fly (creative or /fly) and then you fall, your distance is bigger
		if (p.getAllowFlight()) {
			maxDist += 1.3;
		}
		//We adapt the distance for the player
		float pingresult = Utility.getPing(p) / 100;
		float toAdd = pingresult / 7;
		maxDist += toAdd;
		//If the player is on ice, his distance is bigger
		if (Utility.getMaterialName(to.clone().add(0, -1, 0)).contains("ice")
				|| Utility.getMaterialName(from.clone().add(0, -1, 0)).contains("ice")
				|| Utility.specificBlockNear(from.clone().add(0, -0.4, 0), "ice")) {
			maxDist *= 1.3;
		}
		sneakAdapter(maxDist, dist, np, xzVelocity, yVelocity);
		waterAdapter(maxDist, dist, np, xzVelocity, yVelocity);
		webAdapter(maxDist, dist, np, xzVelocity, yVelocity);
		if(np.isDevMode()) {
			p.sendMessage("MaxDistance Result: " + (float) maxDist);
		}
		double result = dist - maxDist;
		if (result > 0.1 && !np.isTeleported()) {
			this.punish(event, "MaxDistance: " + dist + " Max: " + maxDist);
		}
	}

	public void sneakAdapter(double maxDist, double dist, NessPlayer p, double xzVelocity, double yVelocity) {
		if (p.getPlayer().isSneaking()) {
			maxDist *= 0.75;
		}
	}

	public void waterAdapter(double maxDist, double dist, NessPlayer p, double xzVelocity, double yVelocity) {
		if (p.getPlayer().getLocation().getBlock().isLiquid()
				&& p.getPlayer().getLocation().clone().add(0, -0.1, 0).getBlock().isLiquid()) {
			maxDist = getMaxWaterSpeed(maxDist);
			maxDist += yVelocity;
			maxDist -= xzVelocity;
			if (p.getPlayer().isSprinting()) {
				maxDist += 0.1f;
			}
		}
	}

	public void webAdapter(double maxDist, double dist, NessPlayer p, double xzVelocity, double yVelocity) {
		if (Utility.getMaterialName(p.getPlayer().getLocation()).contains("web")
				|| Utility.getMaterialName(p.getPlayer().getLocation().clone().add(0, 0.2, 0)).contains("web")
				|| Utility.getMaterialName(p.getPlayer().getLocation().clone().add(0, 0.2, 0)).contains("web")) {
			maxDist *= 0.86;
			if (p.getPlayer().isSprinting()) {
				maxDist *= 1.21f;
			}
			maxDist += xzVelocity;
			maxDist += yVelocity;
		}
	}

	public void Check1(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		float dist = (float) np.getMovementValues().XZDiff; // Our XZ Distance
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		int speedLevel = Utility.getPotionEffectLevel(p, PotionEffectType.SPEED); // We handle the speed potion
		if (speedLevel > 2 || p.getGameMode() == GameMode.SPECTATOR || p.isInsideVehicle() || np.isTeleported()
				|| Utility.hasVehicleNear(p, 3) || Utility.hasflybypass(p)) {
			return;
		}
		// TODO Handle Slowness Potion (Slowness decreases walking speed by 15% × level)
		final double f = to.getYaw() * 0.017453292F;
		double walkSpeed = p.isSprinting() && !p.hasPotionEffect(PotionEffectType.BLINDNESS)
				? (0.3 * p.getWalkSpeed()) / 0.2
				: p.getWalkSpeed();
		final double resultX = Math.abs((Math.sin(f) * walkSpeed)); // We calculate the correct speed Distance for X and
																	// Z coords
		final double resultZ = Math.abs((Math.cos(f) * walkSpeed));
		double maxDist = resultX + resultZ;
		final boolean isInWater = to.getBlock().isLiquid() && to.clone().add(0, -0.1, 0).getBlock().isLiquid();
		final double xzVelocity = (Math.abs(p.getVelocity().getX()) + Math.abs(p.getVelocity().getZ())) * 1.18;
		final double yVelocity = Math.abs(p.getVelocity().getY()) * 0.26;
		maxDist += xzVelocity;
		maxDist += yVelocity;
		if (to.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| from.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| Utility.specificBlockNear(from.clone().add(0, -0.4, 0), "ice")) {
			maxDist *= 1.3;
		}
		// We handle Sneaking
		if (p.isSneaking()) {
			maxDist = adaptWalkSpeed(0.155, p);
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
			maxDist = adaptWalkSpeed(0.172, p);
			if (p.isSprinting()) {
				maxDist *= 1.21f;
			}
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
