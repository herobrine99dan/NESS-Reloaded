package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.utility.Utility;

public class Killaura extends AbstractCheck<EntityDamageByEntityEvent> {

	public Killaura(CheckManager manager) {
		super(manager, CheckInfo.eventOnly(EntityDamageByEntityEvent.class));
	}

	@Override
	void checkEvent(EntityDamageByEntityEvent e) {
		Check(e);
		Check1(e);
		Check3(e);
		Check4(e);
		Check5(e);
		Check6(e);
		Check7(e);
	}

	public void Check(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Vector from = p.getEyeLocation().clone().toVector();
			Vector to = e.getEntity().getLocation().clone().toVector().subtract(e.getEntity().getVelocity());
			Vector result = from.subtract(to);
			double angle = this.isLookingAt(p, e.getEntity().getLocation());
			double maxValue = 3.6D;
			if (p.isSprinting()) {
				maxValue += 0.4D;
			}
			if (Utility.specificBlockNear(e.getDamager().getLocation(), "water")) {
				maxValue += 0.2D;
			}
			if (angle < 0.7) {
				maxValue += 0.3 * angle;
			}
			maxValue += Math.abs(p.getVelocity().getX());
			maxValue += Math.abs(p.getVelocity().getZ());
			if (result.length() > maxValue || result.length() > 5) {
				punish(e, p, 19, "Reach" + "(" + result.length() + ")", 6);
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
				if (Math.abs(grade) > 9350) {
					return;
				}
				if (Math.round(grade) > 360.0) {
					punish(e, p, 19, "HighYaw " + grade, 6);
				}
			}, 3L);
		}
	}

	public void Check3(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.getLocation().getPitch() == Math.round(player.getLocation().getPitch())) {
				punish(event, player, 21, "PerfectAngle", 5);
			} else if (player.getLocation().getYaw() == Math.round(player.getLocation().getYaw())) {
				punish(event, player, 21, "PerfectAngle", 5);
			}
		}
	}

	public void Check4(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			if (isLookingAt(damager, e.getEntity().getLocation()) < 0.1D) {
				punish(e, damager, 23, "Angles/Hitbox " + isLookingAt(damager, e.getEntity().getLocation()), 4);
			}
		}
	}

	public void Check5(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (!p.hasLineOfSight(e.getEntity())) {
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
