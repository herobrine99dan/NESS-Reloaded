package com.github.ness.check;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.DragDown;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utilities;
import com.github.ness.utility.Utility;

public class Speed extends AbstractCheck<PlayerMoveEvent> {
	public HashMap<String, Integer> speed = new HashMap<String, Integer>();
	int maxpackets = 13;

	public Speed(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
		Check1(e);
		Check3(e);
		Check4(e);
	}

	private void punish(PlayerMoveEvent e, String module) {
		Player p = e.getPlayer();
		if (Utility.hasflybypass(p) || manager.getPlayer(e.getPlayer()).isTeleported()) {
			return;
		}
		manager.getPlayer(p).setViolation(new Violation("Speed", module));
		if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
			e.setCancelled(true);
		}
	}

	public void Check(PlayerMoveEvent e) {
		Location from = e.getFrom().clone();
		Location to = e.getTo().clone();
		// Bukkit.getPlayer("herobrine99dan").sendMessage(
		// "Player: " + e.getPlayer().getName() + " YDist: " + Utility.around(to.getY()
		// - from.getY(), 6)
		// + " Dist: " + Utility.around(Utility.getMaxSpeed(from, to), 6));
		Player player = e.getPlayer();
		if (Utility.hasflybypass(player)) {
			return;
		}
		if (Utilities.isStairs(Utilities.getPlayerUnderBlock(player)) || Utilities.isStairs(to.getBlock())) {
			return;
		}
		// player.sendMessage("Time: "+Utility.around(System.currentTimeMillis(), 12));
		if (Utility.specificBlockNear(player.getLocation(), Material.STATIONARY_LAVA)
				|| Utility.specificBlockNear(player.getLocation(), Material.WATER)
				|| Utility.specificBlockNear(player.getLocation(), Material.LAVA)
				|| Utility.specificBlockNear(player.getLocation(), Material.STATIONARY_WATER)
				|| Utility.hasflybypass(player) || Utility.specificBlockNear(player.getLocation(), Material.SNOW)) {
			return;
		}
		if (!player.getNearbyEntities(5, 5, 5).isEmpty()) {
			return;
		}
		if (to.add(0, -1, 0).getBlock().getType().name().contains("chest")
				|| from.add(0, -1, 0).getBlock().getType().name().contains("chest")) {
			return;
		}
		if (Utility.getMaterialName(to).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from).toLowerCase().contains("ladder")) {
			return;
		}
		if (Utility.getMaterialName(to.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")) {
			return;
		}
		if (to.add(0, -1, 0).getBlock().getType().name().contains("detector")
				|| from.add(0, -1, 0).getBlock().getType().name().contains("detector")) {
			return;
		}
		if (!player.isInsideVehicle() && !player.isFlying() && !player.hasPotionEffect(PotionEffectType.JUMP)) {
			if (to.getY() > from.getY()) {
				double y = Utility.around(to.getY() - from.getY(), 6);

				ArrayList<Block> blocchivicini = Utility.getSurrounding(Utilities.getPlayerUnderBlock(player), false);
				boolean bypass = false;
				for (Block s : blocchivicini) {
					if (s.getType().equals(Material.SLIME_BLOCK)) {
						bypass = true;
					}
				}
				if (y > 0.36 && y < 0.419 && !(y == 0.404) && !(y == 0.365) && !(y == 0.395) && !bypass && !(y == 0.386)
						&& !(y == 0.414) && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
					punish(e, "MiniJump1 " + y);
				} else if (y > 0.248 && y < 0.333 && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
					punish(e, "MiniJump2 " + y);
				}
			}
		}
	}

	// To recode
	public void Check1(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (Utility.hasflybypass(player)) {
			return;
		}
		double soulsand = 0.22;
		if (Utility.hasflybypass(player)) {
			return;
		}
		if (this.manager.getPlayer(player).isTeleported()) {
			return;
		}
		double dist =  this.manager.getPlayer(player).getMovementValues().XZDiff;
		if (Utility.isMathematicallyOnGround(e.getTo().getY()) && !player.isInsideVehicle() && !player.isFlying()
				&& !player.hasPotionEffect(PotionEffectType.SPEED) && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			if (dist > 0.62D) {
				if (Utilities.getPlayerUpperBlock(player).getType().isSolid()
						&& e.getTo().clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
						&& e.getFrom().clone().add(0, -1, 0).getBlock().getType().name().toLowerCase()
								.contains("ice")) {
					return;
				}
				punish(e, "MaxDistance " + dist);
			} else if (dist > soulsand && player.getLocation().getBlock().getType().equals(Material.SOUL_SAND)
					&& Utility.isMathematicallyOnGround(e.getFrom().getY()) && player.getFallDistance() == 0.0
					&& e.getTo().getY() - e.getFrom().getY() == 0.0) {
				punish(e, "NoSlowDown " + dist);
			}
		}
	}

	public void Check3(PlayerMoveEvent e) {
		Location to = e.getTo().clone();
		Location from = e.getFrom().clone();
		NessPlayer np = this.manager.getPlayer(e.getPlayer());
		Player p = e.getPlayer();
		double y = to.getY() - from.getY();
		double yresult = y - p.getVelocity().getY();
		// Vector result = v.subtract(p.getVelocity());
		if (Utility.hasflybypass(p) || Utility.hasBlock(p, Material.SLIME_BLOCK) || Utility.hasWater(p)
				|| Utility.isInWater(p)) {
			return;
		}
		if (Utility.getMaterialName(to).contains("water") || Utility.getMaterialName(to).contains("lava")
				|| Utility.getMaterialName(from).contains("water") || Utility.getMaterialName(from).contains("water")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("ladder") || Utility.getMaterialName(from).contains("ladder")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("web") || Utility.getMaterialName(from).contains("web")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("stairs") || Utility.getMaterialName(from).contains("stairs")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("vine") || Utility.getMaterialName(from).contains("vine")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("fence") || Utility.getMaterialName(from).contains("fence")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("wall") || Utility.getMaterialName(from).contains("wall")) {
			return;
		}
		if (Utility.getMaterialName(to).contains("carpet") || Utility.getMaterialName(from).contains("carpet")) {
			return;
		}
		if (Utility.getMaterialName(to.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")) {
			return;
		}
		if (Math.abs(yresult) > 0.9) {
			if (Utility.hasflybypass(p) || manager.getPlayer(e.getPlayer()).isTeleported()) {
				return;
			}
			np.InvalidVelocitySpeedCounter++;
			if (np.InvalidVelocitySpeedCounter < 3) {
				return;
			}
			manager.getPlayer(p).setViolation(new Violation("Speed", "InvalidVelocity: " + yresult));
			if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
				if (!DragDown.playerDragDown(p)) {
					e.setCancelled(true);
				}
			}
		}
	}

	/**
	 * The Speed Prediction Check of Jonhan, with some changes From
	 * https://www.youtube.com/watch?v=QXukRdPlXn4&t=416s
	 * 
	 * @param e
	 */
	public void Check4(PlayerMoveEvent e) {
		Location to = e.getTo().clone();
		Location from = e.getFrom().clone();
		NessPlayer np = this.manager.getPlayer(e.getPlayer());
		double dist = this.manager.getPlayer(e.getPlayer()).getMovementValues().xzDiffMultiplier;
		double lastDist = np.lastSpeedPredictionDist;
		np.lastSpeedPredictionDist = dist;
		boolean lastOnGround = np.lastSpeedPredictionOnGround;
		np.lastSpeedPredictionOnGround = Utility.isMathematicallyOnGround(to.getY());
		float friction = 0.91F;
		if (Utility.getMaterialName(to).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from).toLowerCase().contains("ladder")) {
			return;
		}
		if (Utility.getMaterialName(to.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")
				|| Utility.getMaterialName(from.clone().add(0, 0.5, 0)).toLowerCase().contains("ladder")) {
			return;
		}
		double shiftedLastDist = lastDist * friction;
		double equalness = dist - shiftedLastDist;
		float scaledEqualness = (float) (equalness * 136);
		if (!Utility.isMathematicallyOnGround(to.getY()) && !lastOnGround) {
			if (scaledEqualness > 1.1) {
				this.punish(e, "InvalidFriction: " + scaledEqualness);
			}
		}
	}
}
