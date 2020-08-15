package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
		Check2(e);
		Check3(e);
		Check4(e);
		Check5(e);
		Check6(e);
	}

	public void Check(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Entity entity = e.getEntity();
			double range = Math.hypot(p.getLocation().getX() - entity.getLocation().getX(),
					p.getLocation().getZ() - entity.getLocation().getZ());
			double maxReach = 3.4D;
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				maxReach = 5.4D;
			}
			if (!p.isSprinting() || isLookingAt(p, entity.getLocation()) < 0.6
					|| Utility.specificBlockNear(e.getDamager().getLocation(), "water")
					|| Utility.yawTo180F(p.getLocation().getYaw() - entity.getLocation().getYaw()) <= 90) {
				maxReach += 0.2D;
			}
			maxReach += (Utility.getPing(p) / 100) / 10;
			range -= Math.abs(p.getVelocity().getX()) + Math.abs(p.getVelocity().getZ());
			range -= Math.abs(entity.getVelocity().getX()) + Math.abs(entity.getVelocity().getZ());
			if ((range > maxReach && range < 6.5D)
					|| Utility.getHorizontalDistance(p.getLocation(), entity.getLocation()) > 5) {
				this.punish(e, p, "Reach: " + range);
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
				if (Math.round(grade) > 340.0) {
					punish(e, p, "HighYaw " + grade);
				}
			}, 3L);
		}
	}

	public void Check2(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.getLocation().getPitch() == Math.round(player.getLocation().getPitch())) {
				punish(event, player, "PerfectAngle");
			} else if (player.getLocation().getYaw() == Math.round(player.getLocation().getYaw())) {
				punish(event, player, "PerfectAngle");
			}
		}
	}

	public void Check3(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			if (isLookingAt(damager, e.getEntity().getLocation()) < -0.2D) {
				punish(e, damager, "Angles/Hitbox " + isLookingAt(damager, e.getEntity().getLocation()));
			}
		}
	}

	public void Check4(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (!p.hasLineOfSight(e.getEntity())) {
				Block b = p.getTargetBlock(null, 5);
				if (!Utility.getMaterialName(b.getLocation()).contains("slab") && b.getType().isSolid()
						&& b.getType().isOccluding()) {
					punish(e, p, "WallHit");
				}
			}
		}
	}

	public void Check5(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity().getEntityId() == e.getDamager().getEntityId()) {
				punish(e, (Player) e.getEntity(), "SelfHit");
			}
		}
	}

	public void Check6(EntityDamageByEntityEvent e) {
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

	private void punish(EntityDamageByEntityEvent e, Player p, String module) {
		if (manager.getPlayer(p).shouldCancel(e, this.getClass().getSimpleName())) {
			e.setCancelled(true);
		}
		manager.getPlayer(p).setViolation(new Violation("Killaura", module));
	}

}
