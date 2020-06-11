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
		Check2(e);
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
		try {
			ConfigurationSection cancelsec = manager.getNess().getNessConfig().getViolationHandling()
					.getConfigurationSection("cancel");
			if (manager.getPlayer(p).checkViolationCounts.getOrDefault((this.getClass().getSimpleName()), 0) > cancelsec
					.getInt("vl", 10)) {
				e.setCancelled(true);
			}
		} catch (Exception ex) {
		}
	}

	public void Check(PlayerMoveEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
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
					if (Utility.hasflybypass(player)) {
						return;
					}
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
		double soulsand = 0.211;
		if (Utility.hasflybypass(player)) {
			return;
		}
		double dist = Utility.getMaxSpeed(e.getFrom(), e.getTo());
		if (Utility.checkGround(e.getTo().getY()) && !player.isInsideVehicle() && !player.isFlying()
				&& !player.hasPotionEffect(PotionEffectType.SPEED) && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			if (dist > 0.62D) {
				if (Utilities.getPlayerUpperBlock(player).getType().isSolid()
						&& Utilities.getLocationUnderBlock(e.getTo()).getType().name().toLowerCase().contains("ice")
						&& Utilities.getLocationUnderBlock(e.getFrom()).getType().name().toLowerCase()
								.contains("ice")) {
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

	public void Check2(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (Utility.hasflybypass(p)) {
			return;
		}
		int ping = Utility.getPing(p);
		int maxPackets = maxpackets * (ping / 100);
		if (ping < 150) {
			maxPackets = maxpackets;
		}
		NessPlayer player = manager.getPlayer(p);
		if (player.getOnMoveRepeat() > maxPackets) {
			punish(event, "Timer");
			// p.sendMessage("Repeat: " + player.getOnMoveRepeat());
		}
	}
	//Can Be Better
	public void Check3(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location from = e.getFrom();
		Player p = e.getPlayer();
		NessPlayer np = this.manager.getPlayer(p);
		double x = to.getX() - from.getX();
		double y = to.getY() - from.getY();
		if (Double.toString(y).length() > 4) {
			y = Utility.around(y, 5);
		}
		double z = to.getZ() - from.getZ();
		Vector v = new Vector(x, y, z);
		// Vector result = v.subtract(p.getVelocity());
		Vector result = v.subtract(p.getVelocity().setY(Utility.around(p.getVelocity().getY(), 5)));
		double yresult = 0.0;
		if (Utility.checkGround(to.getY())) {
			return;
		}
		if (Utility.hasflybypass(p)) {
			return;
		}
		try {
			yresult = Utility.around(result.getY(), 5);
		} catch (Exception ex) {
			yresult = result.getY();
		}
		if (!Utilities.isAround(to, to.getBlock().getType())) {
			return;
		}
		if (!(yresult == 0.07)) {
			if (!(yresult == 0.0)) {
				if (!(yresult == -0.01)) {
					if (!(yresult == -0.03)) {
						if (Math.abs(yresult) > 0.36) {
							punish(e, "InvalidVelocity " + yresult);
						} else if (Math.abs(yresult) > 0.06) {
							if (np.isDevMode()) {
								p.sendMessage("YResult: " + yresult);
							}
						}
					}
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
		if (Utilities.isClimbableBlock(event.getFrom().getBlock())
				|| Utilities.isClimbableBlock(event.getTo().getBlock())
				|| Utility.blockAdjacentIsLiquid(event.getFrom()) || Utility.blockAdjacentIsLiquid(event.getTo())) {
			return;
		}
		if (delta != 0) {
			if (p.isDevMode()) {
				//event.getPlayer().sendMessage("Delta: " + delta + " LastDelta: " + p.lastYDelta);
			}
			if (delta == -p.lastYDelta || delta == p.lastYDelta) {
				punish(event, "RepeatedMovement: " + delta);

			}
		}
		p.lastYDelta = delta;
	}

}
