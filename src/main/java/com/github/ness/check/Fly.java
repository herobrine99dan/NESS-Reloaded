package com.github.ness.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.github.ness.CheckManager;
import com.github.ness.DragDown;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utilities;
import com.github.ness.utility.Utility;

public class Fly extends AbstractCheck<PlayerMoveEvent> {

	protected HashMap<String, Integer> noground = new HashMap<String, Integer>();

	public Fly(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(PlayerMoveEvent.class));
	}

	@Override
	void checkEvent(PlayerMoveEvent e) {
		Check(e);
		Check1(e);
		Check4(e);
		Check8(e);
		Check16(e);
		Check17(e);
		Check20(e);
	}

	protected List<String> bypasses = Arrays.asList("slab", "stair", "snow", "bed", "skull", "step", "slime");

	public void punish(PlayerMoveEvent e, Player p, String module) {
		if (!Utility.hasflybypass(p) && !this.manager.getPlayer(p).isTeleported()) {
			manager.getPlayer(p).setViolation(new Violation("Fly", module));
			try {
				if (manager.getPlayer(e.getPlayer()).shouldCancel(e, this.getClass().getSimpleName())) {
					if (!DragDown.playerDragDown(p)) {
						e.setCancelled(true);
					}
				}
			} catch (Exception ex) {
				e.setCancelled(true);
			}
		}
	}

	/**
	 * Check to detect no velocity(normal velocity should be upper than -0.06) This
	 * detect stupid fly
	 * 
	 * @param event
	 */
	public void Check(PlayerMoveEvent event) {
		if (!bypass(event.getPlayer()) && !Utility.hasBlock(event.getPlayer(), Material.SLIME_BLOCK)) {
			Player player = event.getPlayer();
			if (!event.getPlayer().isOnGround()) {
				double fallDist = event.getPlayer().getFallDistance();
				if (event.getPlayer().getVelocity().getY() < -1.0D && fallDist == 0.0D) {
					punish(event, player, "NoVelocity");
				}
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
			if (Utilities.isClimbableBlock(p.getLocation().getBlock()) && !Utilities.isInWater(p)) {
				double distance = Utility.around(event.getTo().getY() - event.getFrom().getY(), 6);
				ConfigurationSection config = this.manager.getNess().getNessConfig().getCheck(this.getClass());
				double maxDistance = config.getDouble("maxladderdistance", 0.12);
				if (distance > maxDistance && distance == Utility.around(np.lastYDelta, 6)) {
					punish(event, p, "FastLadder: " + distance);
				} else if (distance > maxDistance + 0.02 && (Math.abs(np.lastYDelta - distance) < 0.05)) {
					punish(event, p, "FastLadder: " + distance);
				}
			}
		}
	}

	/**
	 * Check to detect AirJump
	 * 
	 * @param e
	 */
	// BadCheck
	public void Check4(PlayerMoveEvent e) {
		final Location from = e.getFrom();
		final Location to = e.getTo();
		double fromy = e.getFrom().getY();
		double toy = e.getTo().getY();
		final Player p = e.getPlayer();
		final double defaultvalue = 0.08307781780646906D;
		final double defaultjump = 0.41999998688697815D;
		final double distance = toy - fromy;
		if (!bypass(e.getPlayer()) && !from.getBlock().getType().isSolid() && !to.getBlock().getType().isSolid()) {
			Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
				if (to.getY() > from.getY()) {
					if (distance > defaultjump) {
						ArrayList<Block> blocchivicini = Utility.getSurrounding(Utilities.getPlayerUnderBlock(p), true);
						boolean bypass = Utility.hasBlock(p, Material.SLIME_BLOCK);
						Iterator<Block> var4 = blocchivicini.iterator();

						while (var4.hasNext()) {
							Block s = var4.next();
							Iterator<String> var6 = bypasses.iterator();

							while (var6.hasNext()) {
								String b = var6.next();
								if (s.getType().toString().toLowerCase().contains(b)) {
									bypass = true;
								}
							}
						}

						if (!bypass) {
							punish(e, p, "AirJump");
						}
					} else if (distance == defaultvalue || distance == defaultjump) {
						Location loc = p.getLocation();
						Location loc1 = p.getLocation();
						loc1.setY(loc.getY() - 2.0D);
						if (loc1.getBlock().getType() == Material.AIR
								&& Utilities.getPlayerUnderBlock(p).getType().equals(Material.AIR)
								&& p.getVelocity().getY() <= -0.078D
								&& !loc.getBlock().getType().name().contains("STAIR")
								&& !loc1.getBlock().getType().name().contains("STAIR") && p.getNoDamageTicks() <= 1) {
							punish(e, p, "AirJump1");
						}
					}
				}

			}, 2L);
		}
	}

	/**
	 * Check for abnormal ground packet
	 * 
	 * @param e
	 */
	public void Check8(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (Bukkit.getVersion().contains("1.8")) {
			return;
		}
		if (!bypass(e.getPlayer()) && player.getNearbyEntities(2, 2, 2).isEmpty()) {
			if (player.isOnline() && !Utility.hasBlock(player, Material.SLIME_BLOCK)) {
				if (player.isOnGround() && !Utility.isOnGround(e.getTo())) {
					punish(e, player, "FalseGround");
				} else if (player.isOnGround() && !Utility.isMathematicallyOnGround(e.getTo().getY())) {
					punish(e, player, "FalseGround");
				}
			}

		}
	}

	/**
	 * Check for high pitch and yaw
	 * 
	 * @param e
	 */
	public void Check16(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (player.getLocation().getYaw() > 360.0f || player.getLocation().getYaw() < -360.0f
				|| player.getLocation().getPitch() > 90.0f || player.getLocation().getPitch() < -90.0f) {
			punish(e, player, "IllegalMovement");
		}
	}

	/**
	 * Check for high web distance
	 * 
	 * @param e
	 */
	public void Check17(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if (player.isFlying() || player.hasPotionEffect(PotionEffectType.SPEED)) {
			return;
		}
		ConfigurationSection config = this.manager.getNess().getNessConfig().getCheck(this.getClass());
		Double hozDist = Utility.getMaxSpeed(from, to);
		double maxDist = config.getDouble("maxwebdistance", 0.2);
		if (!Utility.isMathematicallyOnGround(to.getY())) {
			maxDist += Math.abs(player.getVelocity().getY()) * 0.4;
		}
		if (from.getBlock().getType() == Material.WEB && hozDist > maxDist) {
			punish(e, player, "NoWeb");
			// player.sendMessage("NoWebDist: " + hozDist);
		}
	}

	public void Check20(PlayerMoveEvent e) {
		double yDist = e.getTo().getY() - e.getFrom().getY();
		ConfigurationSection config = this.manager.getNess().getNessConfig().getCheck(this.getClass());
		if (yDist > config.getDouble("maxydist", 0.7) && !bypass(e.getPlayer())) {
			punish(e, e.getPlayer(), "HighDistance");
		}
	}

	public boolean bypass(Player p) {
		if (p.isInsideVehicle()) {
			return false;
		}
		if (p.hasPotionEffect(PotionEffectType.SPEED) || p.hasPotionEffect(PotionEffectType.JUMP)) {
			return false;
		}
		if (Utilities.isInWeb(p)) {
			return false;
		}
		if (Utility.hasflybypass(p) || this.manager.getPlayer(p).isTeleported()) {
			return false;
		}
		if (Utilities.getPlayerUnderBlock(p).getType().equals(Material.LADDER)
				&& !Utilities.getPlayerUnderBlock(p).getType().equals(Material.VINE)
				&& Utilities.getPlayerUnderBlock(p).getType().equals(Material.WATER)) {
			return false;
		}
		for (Block b : Utility.getSurrounding(p.getLocation().getBlock(), true)) {
			if (b.getType().isSolid()) {
				return false;
			}
		}
		return true;
	}
}
