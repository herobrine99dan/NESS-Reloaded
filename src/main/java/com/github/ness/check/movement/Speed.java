package com.github.ness.check.movement;

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
import com.github.ness.utility.Utility;

public class Speed extends AbstractCheck<PlayerMoveEvent> {

	public Speed(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
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
		final double resultX = Math.abs((Math.sin(f) * walkSpeed));
		final double resultZ = Math.abs((Math.cos(f) * walkSpeed));
		double maxDist = resultX + resultZ;
		if (to.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| from.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| Utility.specificBlockNear(from.clone().add(0, -0.4, 0), "ice")) {
			maxDist *= 1.3;
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
		final double yVelocity = Math.abs(p.getVelocity().getY()) * 0.30;
		if ((checkSneak(p, dist, maxDist, yVelocity) || checkWeb(p, dist, maxDist, yVelocity)) && !np.isTeleported()) {
			this.punish(event, "MaxDistance" + maxDist);
		}
	}
	
	public boolean checkWeb(Player p, double dist, double maxDist, double yVelocity) {
		if (Utility.getMaterialName(p.getLocation().clone().add(0, 0.2, 0)).contains("web")) {
			maxDist *= 0.85;
			maxDist += yVelocity * 0.30;
			if ((dist - maxDist) > 0.1) {
				return true;
			}
		}
		return false;
	}

	public boolean checkSneak(Player p, double dist, double maxDist, double yVelocity) {
		if (p.isSneaking()) {
			maxDist *= 0.75;
			maxDist += yVelocity * 0.30;
			if ((dist - maxDist) > 0.1) {
				return true;
			}
		}
		return false;
	}

	private void punish(PlayerMoveEvent e, String module) {
		Player p = e.getPlayer();
		if (Utility.hasflybypass(p) || manager.getPlayer(e.getPlayer()).isTeleported()) {
			return;
		}
		manager.getPlayer(p).setViolation(new Violation("Speed", module), e);
	}

	private double getMaxWaterSpeed(Player p) {
		if (NESSAnticheat.getInstance().getMinecraftVersion() > 1122) {
			return p.getWalkSpeed();
		}
		return 0.14;
	}
}
