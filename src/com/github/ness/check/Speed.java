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
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.PlayerManager;

import com.github.ness.CheckManager;
import com.github.ness.MovementPlayerData;
import com.github.ness.NessPlayer;
import com.github.ness.Utilities;
import com.github.ness.Utility;
import com.github.ness.Violation;

public class Speed extends AbstractCheck<PlayerMoveEvent> {
	public  HashMap<String, Integer> speed = new HashMap<String, Integer>();

	public Speed(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	void checkEvent(PlayerMoveEvent e) {
       Check(e);
       Check1(e);
       Check2(e);
       Check4(e);
	}
	
	private void punish(Player p) {
		manager.getPlayer(p).setViolation(new Violation("Speed"));
	}
	
	public  void Check(PlayerMoveEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
		// Bukkit.getPlayer("herobrine99dan").sendMessage(
		// "Player: " + e.getPlayer().getName() + " YDist: " + Utility.around(to.getY()
		// - from.getY(), 6)
		// + " Dist: " + Utility.around(Utility.getMaxSpeed(from, to), 6));
		Player player = e.getPlayer();
		if (Utilities.isStairs(Utilities.getPlayerUnderBlock(player)) || Utilities.isStairs(to.getBlock())) {
			return;
		}
		// player.sendMessage("Time: "+Utility.around(System.currentTimeMillis(), 12));
		if (Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_LAVA)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.WATER)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.LAVA)
				|| Utility.SpecificBlockNear(player.getLocation(), Material.STATIONARY_WATER)
				|| Utility.hasflybypass(player)) {
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
					punish(player);
					if (NESS.main.devMode) {
						player.sendMessage("y:" + y);
					}
				} else if (y > 0.248 && y < 0.333 && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
					punish(player);
					if (NESS.main.devMode) {
						player.sendMessage("Ydist: " + y);
					}
				}
			}
		}
	}

	public  void Check1(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (Utility.hasflybypass(player)) {
			return;
		}
		double soulsand = 0.211;
		double dist = Utility.getMaxSpeed(event.getFrom(), event.getTo());
		if (player.isOnGround() && !player.isInsideVehicle() && !player.isFlying()
				&& !player.hasPotionEffect(PotionEffectType.SPEED) && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
			if (dist > 0.62D) {
				if (Utilities.getPlayerUpperBlock(player).getType().isSolid()
						&& Utilities.getPlayerUnderBlock(player).getType().name().toLowerCase().contains("ice")) {
					return;
				}
				if (NESS.main.devMode) {
					event.getPlayer().sendMessage("First Distance: " + dist);
				}
				punish(player);
			} else if (dist > soulsand && player.getFallDistance() == 0
					&& player.getLocation().getBlock().getType().equals(Material.SOUL_SAND)) {
				punish(player);
			}
		}
	}

	public  void Check2(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (Utility.hasflybypass(p)) {
			return;
		}
		int ping = PlayerManager.getPing(p);
		int maxPackets = NESS.main.maxpackets * (ping / 100);
		if (ping < 150) {
			maxPackets = NESS.main.maxpackets;
		}
		NessPlayer player = new NessPlayer(p);
		if (player.getOnMoveRepeat() > maxPackets) {
			punish(p);
			// p.sendMessage("Repeat: " + player.getOnMoveRepeat());
		}
	}

	public  void Check4(PlayerMoveEvent event) {
		MovementPlayerData mp = MovementPlayerData.getInstance(event.getPlayer());
		Player player = event.getPlayer();

		if (Utility.hasflybypass(player))
			return;

		double ydiff = event.getTo().getY() - event.getFrom().getY();

		if (!(mp==null)) {
			if (Utilities.IsSameBlockAround(player, Material.AIR, 2, 0.5f)
					&& Utilities.IsSameBlockAround(player, Material.AIR, 0, 0.5f)
					&& player.getLocation().add(0, -1, 0).getBlock().getType() != Material.SLIME_BLOCK) {
				if (mp.getLastYDiff() <= 0.f && ydiff > 0.f && ydiff < 0.4
						|| ydiff != 0.f && ydiff == -(mp.getLastYDiff())) {
					punish(player);
				}
			}
		}
		mp.setLastYDiff(ydiff);
	}

	private  Vector getHV(Vector V) {
		V.setY(0);
		return V;
	}

}
