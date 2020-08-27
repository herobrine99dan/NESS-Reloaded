package com.github.ness.check;

import java.util.concurrent.TimeUnit;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Speed extends AbstractCheck<PlayerMoveEvent> {
	
	double maxInvalidVelocity;

	public Speed(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(PlayerMoveEvent.class, 1, TimeUnit.SECONDS));
		this.maxInvalidVelocity = this.manager.getNess().getNessConfig().getCheck(this.getClass())
				.getDouble("maxinvalidvelocity", 0.9);
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check1(e);
		Check2(e);
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
		if (Utility.hasflybypass(p)) {
			return;
		}
		NessPlayer np = this.manager.getPlayer(p);
		if (np.isTeleported() || Utility.hasVehicleNear(p, 3) || Utility.hasLivingEntityNear(p, 3)) {
			return;
		}
		float dist = (float) np.getMovementValues().XZDiff;
		Location to = event.getTo().clone();
		Location from = event.getFrom().clone();
		if (p.getGameMode() == GameMode.SPECTATOR || p.isInsideVehicle()) {
			return;
		}
		int speedLevel = Utility.getPotionEffectLevel(p, PotionEffectType.SPEED);
		if (speedLevel > 2) {
			return;
		}
		float f = to.getYaw() * 0.017453292F;
		float resultX = Math.abs((float) (Math.sin(f) * p.getWalkSpeed()));
		float resultZ = Math.abs((float) (Math.cos(f) * p.getWalkSpeed()));
		float maxDist = resultX + resultZ;
		final boolean isInWater = to.getBlock().isLiquid() && to.clone().add(0, -0.1, 0).getBlock().isLiquid();
		float xVelocity = (float) p.getVelocity().getX();
		float zVelocity = (float) p.getVelocity().getZ();
		maxDist += (float) (Math.abs(zVelocity) + Math.abs(xVelocity)) * 1.14;
		maxDist += (float) Math.abs(p.getVelocity().getY()) * 0.16;
		if (p.isSprinting() && Utility.isMathematicallyOnGround(to.getY())
				&& Utility.isMathematicallyOnGround(from.getY())) {
			maxDist = 0.56f;
		} else if (p.isSprinting()) {
			maxDist *= 1.24f;
		}
		if (to.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| from.clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
				|| Utility.specificBlockNear(from.clone().add(0, -0.4, 0), "ice")) {
			maxDist *= 1.35f;
		}
		if (p.isSneaking()) {
			maxDist = 0.172f;
			maxDist += (float) (Math.abs(zVelocity) + Math.abs(xVelocity)) * 1.14;
			maxDist += (float) Math.abs(p.getVelocity().getY()) * 0.16;
		}
		if (isInWater) {
			maxDist = getMaxWaterSpeed();
			maxDist += (float) Math.abs(p.getVelocity().getY()) * 0.26f;
			if (p.isSprinting()) {
				maxDist += 0.1f;
			}
		}

		if (Utility.getMaterialName(to).contains("web") || Utility.getMaterialName(from).contains("web")
				|| Utility.getMaterialName(to.clone().add(0, 0.2, 0)).contains("web")
				|| Utility.getMaterialName(from.clone().add(0, 0.2, 0)).contains("web")) {
			maxDist = 0.2f;
			if (p.isSprinting()) {
				maxDist *= 1.21f;
			}
			maxDist += (float) (Math.abs(zVelocity) + Math.abs(xVelocity)) * 1.14;
			maxDist += (float) Math.abs(p.getVelocity().getY()) * 0.17;
		}
		if (Utility.getMaterialName(to).contains("stairs") || Utility.getMaterialName(from).contains("stairs")
				|| Utility.getMaterialName(to.clone().add(0, 0.3, 0)).contains("stairs")
				|| Utility.getMaterialName(to.clone().add(0, -0.3, 0)).contains("stairs")) {
			maxDist = 0.45f;
			if (p.isSprinting()) {
				maxDist *= 1.26f;
			}
			maxDist += (float) (Math.abs(zVelocity) + Math.abs(xVelocity)) * 1.14;
			maxDist += (float) Math.abs(p.getVelocity().getY()) * 0.18;
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
		float result = dist - maxDist;
		// p.sendMessage("maxDist: " + maxDist + " Dist: " + dist);
		if (result > 0.1 && !np.isTeleported()) {
			this.punish(event, "MaxDistance: " + dist + " Max: " + maxDist);
		}
	}

	private float getMaxWaterSpeed() {
		if (NESSAnticheat.getInstance().getMinecraftVersion() > 1122) {
			return 0.2f;
		}
		return 0.12f;
	}

	public void Check2(PlayerMoveEvent e) {
		NessPlayer np = this.manager.getPlayer(e.getPlayer());
		Player p = e.getPlayer();
		double y = np.getMovementValues().yDiff;
		double yresult = y - p.getVelocity().getY();
		if (Utility.hasflybypass(p) || Utility.hasBlock(p, "slime") || p.getAllowFlight()
				|| Utility.specificBlockNear(e.getTo().clone().add(0, -0.3, 0), "lily")) {
			return;
		}
		double max = 0.9;
		float pingresult = Utility.getPing(p) / 100;
		float toAdd = pingresult / 4;
		max += toAdd;
		if (Math.abs(yresult) > max && !manager.getPlayer(e.getPlayer()).isTeleported()) {
			this.punish(e, "InvalidVelocity: " + yresult);
		}
	}

}
