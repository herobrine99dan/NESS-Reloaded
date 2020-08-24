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
	}

	public void Check(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player player = (Player) e.getDamager();
			Entity entity = e.getEntity();
			NessPlayer np = this.manager.getPlayer(player);
			double range = Math.hypot(np.getMovementValues().getTo().getX() - entity.getLocation().getX(),
					np.getMovementValues().getTo().getZ() - entity.getLocation().getZ());
			double maxReach = 3.4D;
			if (player.getGameMode().equals(GameMode.CREATIVE)) {
				maxReach = 5.5D;
			}
			if (!player.isSprinting() || isLookingAt(player, entity.getLocation()) < 0.6
					|| Utility.specificBlockNear(e.getDamager().getLocation(), "water")
					|| Utility.yawTo180F(np.getMovementValues().getTo().getYaw() - entity.getLocation().getYaw()) <= 90) {
				maxReach += 0.4D;
			}
			maxReach += (Utility.getPing(player) / 100) / 10;
			maxReach += (Math.abs(player.getVelocity().getY()) + Math.abs(player.getVelocity().getY())) * 0.25;
			maxReach += Math.abs(player.getVelocity().getX()) + Math.abs(player.getVelocity().getZ());
			maxReach += Math.abs(entity.getVelocity().getX()) + Math.abs(entity.getVelocity().getZ());
			if ((range > maxReach && range < 6.5D)
					|| Utility.getDistance3D(player.getLocation(), entity.getLocation()) > 5) {
				this.punish(e, player, "Reach: " + range);
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
				punish(event, player, "PerfectAngle1");
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

	private double isLookingAt(Player player, Location target) {
		Location eye = player.getEyeLocation();
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(getDirection(eye));

		return dot;// dot > 0.99D
	}
	
	private static Vector getDirection(Location loc) {
		Vector vector = new Vector();
		double rotX = loc.getYaw();
		double rotY = 3;
		vector.setY(-Math.sin(Math.toRadians(rotY)));
		double xz = Math.cos(Math.toRadians(rotY));
		vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
		vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
		return vector;
	}

	private void punish(EntityDamageByEntityEvent e, Player p, String module) {
		manager.getPlayer(p).setViolation(new Violation("Killaura", module), e);
	}

}
