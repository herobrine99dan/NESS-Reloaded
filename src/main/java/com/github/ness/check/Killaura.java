package com.github.ness.check;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Killaura extends AbstractCheck<EntityDamageByEntityEvent> {
	public HashMap<Player, Entity> lastEntityHit = new HashMap<Player, Entity>();
	public HashMap<String, String> mobinfront = new HashMap<String, String>();

	public Killaura(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
		Check1(e);
		Check2(e);
		Check3(e);
		Check4(e);
		Check5(e);
		Check6(e);
		Check7(e);
	}

	public void Check(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Entity et = e.getEntity();
			double xDist = Math.abs(p.getLocation().getX() - et.getLocation().getX());
			double zDist = Math.abs(p.getLocation().getZ() - et.getLocation().getZ());
			double dist = Utility.getMaxSpeed(p.getLocation(), et.getLocation());
			if (dist > 5 || xDist > 3.1 || zDist > 3.1) {
				punish(e, p, 19, "Reach" + "(" + dist + ")", 6);
			}
		}
	}

	public void Check1(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			final Location loc = p.getLocation();
			Bukkit.getScheduler().runTaskLater(manager.getNess(), () -> {
				Location loc1 = p.getLocation();
				float grade = loc.getYaw() - loc1.getYaw();
				if (Math.round(grade) > 300.0) {
					punish(e, p, 19, "HighYaw " + grade, 6);
				}
			}, 3L);
		}
	}

	public void Check2(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
			Player player = (Player) event.getDamager();
			LivingEntity damaged = (LivingEntity) event.getEntity();
			double offset = 0.0D;

			Location entityLoc = damaged.getLocation().add(0.0D, damaged.getEyeHeight(), 0.0D);
			Location playerLoc = player.getLocation().add(0.0D, player.getEyeHeight(), 0.0D);

			Vector playerRotation = new Vector(playerLoc.getYaw(), playerLoc.getPitch(), 0.0F);
			Vector expectedRotation = Utility.getRotation(playerLoc, entityLoc);

			double deltaYaw = Utility.clamp180(playerRotation.getX() - expectedRotation.getX());
			double deltaPitch = Utility.clamp180(playerRotation.getY() - expectedRotation.getY());

			double horizontalDistance = Utility.getHorizontalDistance(playerLoc, entityLoc);
			double distance = Utility.getDistance3D(playerLoc, entityLoc);

			double offsetX = deltaYaw * horizontalDistance * distance;
			double offsetY = deltaPitch * Math.abs(entityLoc.getY() - playerLoc.getY()) * distance;

			offset += Math.abs(offsetX);
			offset += Math.abs(offsetY);
			if (offset > 300.0D) {
				punish(event, player, 20, "Angles/Hitbox " + offset, 6);
			}
		}
	}

	public void Check3(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.getLocation().getPitch() == Math.round(player.getLocation().getPitch())) {
				punish(event, player, 21, "PerfectAngle", 5);
			}
		}
	}

	public void Check4(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			if (isLookingAt(damager, e.getEntity().getLocation()) < 0.2D) {
				punish(e, damager, 23, "Angles/Hitbox " + isLookingAt(damager, e.getEntity().getLocation()), 4);
			}
		}
	}

	/**
	 * @author theWoosh (https://github.com/projectwoosh) from
	 *         https://github.com/projectwoosh/AntiCheat/blob/master/src/tk/thewoosh/plugins/wac/checks/combat/WallHit.java
	 * @param e
	 */
	public void Check5(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Entity entity = e.getEntity();
			double x = Math.abs(p.getLocation().getX() - e.getEntity().getLocation().getX());
			double z = Math.abs(p.getLocation().getZ() - e.getEntity().getLocation().getZ());

			if (x == 0 || z == 0) {
				return;
			}

			if (p.getLocation().getY() - e.getEntity().getLocation().getY() >= .6) // TODO Change .6 to height of entity
																					// / height of player
				return;

			Location l = null;

			if (x <= .5 && z >= 1) {
				if (p.getLocation().getZ() > entity.getLocation().getZ()) {
					l = p.getLocation().clone().add(0, 0, -1);
				} else {
					l = p.getLocation().clone().add(0, 0, 1);
				}
			} else if (z <= .5 && x >= 1) {
				if (p.getLocation().getX() > entity.getLocation().getX()) {
					l = p.getLocation().clone().add(-1, 0, 0);
				} else {
					l = p.getLocation().clone().add(-1, 0, 0);
				}
			}
			boolean failed = false;

			if (l != null)
				failed = l.getBlock().getType().isSolid() && l.clone().add(0, 1, 0).getBlock().getType().isSolid();
			if (failed) {
				punish(e, p, 23, "WallHit", 4);
			}
		}
	}

	public void Check6(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity().getEntityId() == e.getDamager().getEntityId()) {
				punish(e, (Player) e.getEntity(), 5, "SelfHit", 5);
			}
		}
	}

	public void Check7(EntityDamageByEntityEvent e) {
		if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
			return;
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			NessPlayer np = manager.getPlayer(p);
			float pitch = p.getEyeLocation().getPitch();
			if (np.lastPitch > pitch - 10.5D) {
				// p.sendMessage(String.valueOf(pitch - 10.5D) + " | " + np.lastPitch);
				// p.sendMessage("Cheats!");
			}
			np.lastPitch = pitch;
		}
	}

	private double isLookingAt(Player player, Location target) {
		Location eye = player.getEyeLocation();
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(eye.getDirection());

		return dot;// dot > 0.99D
	}

	private void punish(EntityDamageByEntityEvent e, Player p, int i, String module, int vl) {
		if (manager.getPlayer(p).shouldCancel(e, this.getClass().getSimpleName())) {
			e.setCancelled(true);
		}
		manager.getPlayer(p).setViolation(new Violation("Killaura", module));
	}

}
