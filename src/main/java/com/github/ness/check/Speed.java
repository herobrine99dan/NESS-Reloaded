package com.github.ness.check;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
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
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
		Check1(e);
		Check3(e);
		Check4(e);
		Check5(e);
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
		if (Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_LAVA)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.WATER)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.LAVA)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_WATER)
				|| Utility.hasflybypass(player) || Utility.SpecificBlockNear(player.getLocation(), Material.SNOW)) {
			return;
		}
		if (!player.getNearbyEntities(5, 5, 5).isEmpty()) {
			return;
		}
		if (to.add(0, -1, 0).getBlock().getType().name().contains("chest")
				|| from.add(0, -1, 0).getBlock().getType().name().contains("chest")) {
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
		double dist = Utility.getMaxSpeed(e.getFrom(), e.getTo());
		if (Utility.checkGround(e.getTo().getY()) && !player.isInsideVehicle() && !player.isFlying()
				&& !player.hasPotionEffect(PotionEffectType.SPEED) && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			if (dist > 0.62D) {
				if (Utilities.getPlayerUpperBlock(player).getType().isSolid()
						&& e.getTo().clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")
						&& e.getFrom().clone().add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("ice")) {
					return;
				}
				punish(e, "MaxDistance " + dist);
			} else if (dist > soulsand && player.getLocation().getBlock().getType().equals(Material.SOUL_SAND)
					&& Utility.checkGround(e.getFrom().getY()) && player.getFallDistance() == 0.0
					&& e.getTo().getY() - e.getFrom().getY() == 0.0) {
				punish(e, "NoSlowDown " + dist);
			}
		}
	}

	// Can Be Better
	public void Check3(PlayerMoveEvent e) {
		Location to = e.getTo().clone();
		Location from = e.getFrom().clone();
		Player p = e.getPlayer();
		double x = to.getX() - from.getX();
		double y = to.getY() - from.getY();
		if (Double.toString(y).length() > 4) {
			y = Utility.around(y, 5);
		}
		if (Utility.flagyStuffNear(to.clone()) || Utility.flagyStuffNear(from.clone())) {
			return;
		}
		double z = to.getZ() - from.getZ();
		Vector v = new Vector(x, y, z);
		// Vector result = v.subtract(p.getVelocity());
		Vector result = v.subtract(p.getVelocity().setY(Utility.around(p.getVelocity().getY(), 5)));
		double yresult = 0.0;
		if (Utility.checkGround(to.getY()) && !Utilities.IsSameBlockAround(p, -1f, 0.5f)) {
			return;
		}
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
		try {
			yresult = Utility.around(result.getY(), 5);
		} catch (Exception ex) {
			yresult = result.getY();
		}
		if (!(Math.abs(yresult) < 0.08)) {
			if (Math.abs(yresult) > 0.54) {
				if (Utility.hasflybypass(p) || manager.getPlayer(e.getPlayer()).isTeleported()) {
					return;
				}
				manager.getPlayer(p).setViolation(new Violation("Speed", "InvalidVelocity"));
				if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					e.setCancelled(true);
				}
			}
		}
	}

	public void Check4(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		Player p = e.getPlayer();
		double x = to.getX() - from.getX();
		double z = to.getZ() - from.getZ();
		if (Double.toString(z).length() > 4) {
			z = Utility.around(z, 5);
		}
		if (Double.toString(x).length() > 4) {
			x = Utility.around(x, 5);
		}
		if (Utility.hasflybypass(p)) {
			return;
		}
		if (!Utilities.isAround(to, to.getBlock().getType())) {
			return;
		}
		Vector v = new Vector(x, to.getX(), z);
		// Vector result = v.subtract(p.getVelocity());
		Vector result = v.subtract(p.getVelocity().setY(0));
		double xresult = Math.abs(result.getX());
		double zresult = Math.abs(result.getZ());
		if (xresult > 0.38 || zresult > 0.38) {
			if (!(xresult > 1) && !(zresult > 1) && !(xresult == 0.54) && !(zresult == 0.54)) {
				punish(e, "InvalidXZVelocity");
			}
			// p.sendMessage("X: " + Math.abs(around(result.getX(), 5)) + " Z: " +
			// Math.abs(around(result.getZ(), 5)));
		}
	}

	public void Check5(PlayerMoveEvent event) {
		double delta = event.getTo().getY() - event.getFrom().getY();
		NessPlayer p = manager.getPlayer(event.getPlayer());
		if (Utilities.isClimbableBlock(event.getFrom().getBlock()) || Utility.blockAdjacentIsStair(event.getTo())
				|| Utility.blockAdjacentIsStair(event.getFrom()) || Utilities.isClimbableBlock(event.getTo().getBlock())
				|| Utilities.isOnStairs(event.getPlayer()) || Utility.blockAdjacentIsLiquid(event.getFrom())
				|| Utility.blockAdjacentIsLiquid(event.getTo())) {
			return;
		}
		if (delta != 0) {
			if (p.isDevMode()) {
				// event.getPlayer().sendMessage("Delta: " + delta + " LastDelta: " +
				// p.lastYDelta);
			}
			if (delta == -p.lastYDelta) {
				punish(event, "RepeatedMovement: " + delta);

			}
		}
		p.lastYDelta = delta;
	}
}
