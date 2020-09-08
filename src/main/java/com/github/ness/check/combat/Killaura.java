package com.github.ness.check.combat;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.ness.CheckManager;
import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.AbstractCheck;
import com.github.ness.check.CheckInfo;
import com.github.ness.utility.MathUtils;
import com.github.ness.utility.Utility;

public class Killaura extends AbstractCheck<EntityDamageByEntityEvent> {

	double maxYaw;
	double minAngle;
	double maxReach;

	public Killaura(CheckManager manager) {
		super(manager, CheckInfo.eventWithAsyncPeriodic(EntityDamageByEntityEvent.class, 70, TimeUnit.MILLISECONDS));
		this.maxYaw = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getDouble("maxyaw", 357);
		this.minAngle = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getDouble("minangle", -0.2);
		this.maxReach = this.manager.getNess().getNessConfig().getCheck(this.getClass()).getDouble("maxreach", 3.4);
	}

	@Override
	protected void checkAsyncPeriodic(NessPlayer player) {
		player.attackedEntities.clear();
	}

	@Override
	protected void checkEvent(EntityDamageByEntityEvent e) {
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
			Player player = (Player) e.getDamager();
			Entity entity = e.getEntity();
			NessPlayer np = this.manager.getPlayer(player);
			double range = 0;
			if (entity instanceof Player) {
				NessPlayer damaged = this.getNessPlayer((Player) entity);
				range = Math.hypot(np.getMovementValues().getTo().getX() - damaged.getMovementValues().getTo().getX(),
						np.getMovementValues().getTo().getZ() - damaged.getMovementValues().getTo().getZ());
			} else {
				range = Math.hypot(np.getMovementValues().getTo().getX() - entity.getLocation().getX(),
						np.getMovementValues().getTo().getZ() - entity.getLocation().getZ());
			}
			double maxReach = this.maxReach;
			if (player.getGameMode().equals(GameMode.CREATIVE)) {
				maxReach = 5.5D;
			}
			if (Utility.specificBlockNear(e.getDamager().getLocation(), "water") || MathUtils
					.yawTo180F(np.getMovementValues().getTo().getYaw() - entity.getLocation().getYaw()) <= 90) {
				maxReach += 0.3D;
			}
			maxReach += (Utility.getPing(player) / 100) / 5;
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
				if (Math.round(grade) > maxYaw) {
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
			} else if (player.isDead()) {
				punish(event, player, "Impossible");
			}
		}
	}

	public void Check3(EntityDamageByEntityEvent e) {
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

	public void Check4(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity().getEntityId() == e.getDamager().getEntityId()) {
				punish(e, (Player) e.getDamager(), "SelfHit");
			}
		}
	}

	public void Check5(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity() instanceof LivingEntity) {
				NessPlayer nessPlayer = this.getNessPlayer((Player) e.getDamager());
				nessPlayer.attackedEntities.add(e.getEntity().getEntityId());
				if (nessPlayer.attackedEntities.size() > 2) {
					punish(e, (Player) e.getDamager(), "MultiAura Entities: " + nessPlayer.attackedEntities.size());
				}
			}
		}
	}

	public void Check6(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (e.getEntity() instanceof LivingEntity) {
				NessPlayer nessPlayer = this.getNessPlayer((Player) e.getDamager());
				double angle = Utility.getAngle((Player) e.getDamager(), e.getEntity().getLocation(),
						nessPlayer.getMovementValues().getTo().getDirectionVector());
				if (angle < -0.35) {
					punish(e, (Player) e.getDamager(), "HitBox");
				}
			}
		}
	}

	private void punish(EntityDamageByEntityEvent e, Player p, String module) {
		manager.getPlayer(p).setViolation(new Violation("Killaura", module), e);
	}

}
