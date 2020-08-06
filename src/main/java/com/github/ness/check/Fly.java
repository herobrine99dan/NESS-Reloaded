package com.github.ness.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.DragDown;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Fly extends AbstractCheck<PlayerMoveEvent> {

	protected HashMap<String, Integer> noground = new HashMap<String, Integer>();

	public Fly(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check1(e);
		Check2(e);
		Check3(e);
		Check4(e);
		Check5(e);
		Check6(e);
	}

	protected List<String> bypasses = Arrays.asList("slab", "stair", "snow", "bed", "skull", "step", "slime");

	public void punish(PlayerMoveEvent e, Player p, String module) {
		if (!Utility.hasflybypass(p)) {
			manager.getPlayer(p).setViolation(new Violation("Fly", module));
			try {
				if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					DragDown.playerDragDown(e);
				}
			} catch (Exception ex) {
				e.setCancelled(true);
			}
		}
	}

	/**
	 * Check to detect max distance on ladder
	 * 
	 * @param event
	 */
	public void Check1(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		if (!bypass(event.getPlayer())) {
			if (Utility.isClimbableBlock(p.getLocation().getBlock())) {
				double distance = np.getMovementValues().yDiff;
				if (distance > 0.155D) {
					punish(event, p, "FastLadder: " + distance);
				}
			}
		}
	}

	/**
	 * Check for abnormal ground packet
	 * 
	 * @param e
	 */
	public void Check2(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (Bukkit.getVersion().contains("1.8")) {
			return;
		}
		if (Utility.getMaterialName(e.getTo().clone().add(0, -0.5, 0)).contains("lily")
				|| Utility.getMaterialName(e.getTo().clone().add(0, -0.5, 0)).contains("carpet")) {
			return;
		}
		if (Utility.specificBlockNear(e.getTo(), "lily") || Utility.specificBlockNear(e.getTo(), "snow")) {
			return;
		}
		if (Utility.specificBlockNear(e.getTo(), "carpet")) {
			return;
		}
		if (Utility.getMaterialName(e.getTo().clone()).contains("lily")
				|| Utility.getMaterialName(e.getTo().clone()).contains("carpet")) {
			return;
		}
		if (!bypass(e.getPlayer()) && player.getNearbyEntities(2, 2, 2).isEmpty()) {
			if (player.isOnline() && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
				if (player.isOnGround() && !Utility.isOnGround(e.getTo())) {
					punish(e, player, "FalseGround");
				} else if (player.isOnGround() && !Utility.isMathematicallyOnGround(e.getTo().getY())) {
					punish(e, player, "FalseGround1");
				}
			}

		}
	}

	/**
	 * Check for high pitch and yaw
	 * 
	 * @param e
	 */
	public void Check3(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (e.getTo().getYaw() > 360.0f || e.getTo().getYaw() < -360.0f || e.getTo().getPitch() > 90.0f
				|| e.getTo().getPitch() < -90.0f) {
			punish(e, player, "IllegalMovement");
		}
	}

	/**
	 * Check for high web distance
	 * 
	 * @param e
	 */
	public void Check4(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if (player.isFlying() || player.hasPotionEffect(PotionEffectType.SPEED)) {
			return;
		}
		Double hozDist = this.manager.getPlayer(player).getMovementValues().XZDiff;
		double maxDist = 0.2;
		if (!Utility.isMathematicallyOnGround(to.getY())) {
			maxDist += Math.abs(player.getVelocity().getY()) * 0.4;
		}
		if (from.getBlock().getType() == Material.WEB && hozDist > maxDist) {
			punish(e, player, "NoWeb");
			// player.sendMessage("NoWebDist: " + hozDist);
		}
	}

	public void Check5(PlayerMoveEvent e) {
		double yDist = this.manager.getPlayer(e.getPlayer()).getMovementValues().yDiff;
		if (yDist > 0 && !bypass(e.getPlayer())) {
			double yResult = yDist - e.getPlayer().getVelocity().getY();
			if (yResult > 0.58) {
				punish(e, e.getPlayer(), "HighDistance");
			}
		}
	}

	public void Check6(PlayerMoveEvent event) {
		Location to = event.getTo().clone();
		Player player = event.getPlayer();
		double yDiff = this.manager.getPlayer(player).getMovementValues().yDiff;
		if (yDiff < 0 || Utility.getMaterialName(to.clone().add(0, -0.3, 0)).contains("slab") || Utility.onSteps(to)) {
			return;
		}
		if (player.getVelocity().getY() == 0.42f && yDiff > 0.38) {
			double yResult = Math.abs(yDiff - 0.42f);
			if (yResult > 0.003) {
				punish(event, player, "InvalidJumpMotion");
			}
		}
	}

	public boolean bypass(Player p) {
		if (p.isInsideVehicle()) {
			return true;
		}
		if (p.hasPotionEffect(PotionEffectType.SPEED) || p.hasPotionEffect(PotionEffectType.JUMP)) {
			return true;
		}
		if (Utility.isWeb(p.getLocation())) {
			return true;
		}
		if (Utility.hasflybypass(p) || this.manager.getPlayer(p).isTeleported()) {
			return true;
		}
		if (Utility.getMaterialName(Utility.getPlayerUnderBlock(p).getLocation()).contains("water")) {
			return true;
		}
		for (Block b : Utility.getSurrounding(p.getLocation().getBlock(), true)) {
			if (b.getType().isSolid()) {
				return true;
			}
		}
		return false;
	}
}
